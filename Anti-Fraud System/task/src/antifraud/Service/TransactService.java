package antifraud.Service;

import antifraud.Entity.CardLimit;
import antifraud.Entity.FeedBackDTO;
import antifraud.Entity.Transaction;
import antifraud.Repository.CardLimitRepository;
import antifraud.Repository.IpRepository;
import antifraud.Repository.StolenCardRepository;
import antifraud.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactService {

    private final List<String> regions = List.of("EAP", "ECA", "HIC", "LAC", "MENA", "SA", "SSA");
    private final List<String> feedback = List.of("ALLOWED", "MANUAL_PROCESSING", "PROHIBITED");

    private final TransactionRepository transactionRepository;

    private final IpRepository ipRepository;

    private final StolenCardRepository stolenCardRepository;

    private final CardLimitRepository cardLimitRepository;

    public ResponseEntity<?> Transact(Transaction transaction) {
        if (transaction.getAmount() <= 0 || !isValidCreditCardNumberLuhn(transaction.getNumber()) || !regions.contains(transaction.getRegion()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (cardLimitRepository.findByNumber(transaction.getNumber()) == null)
            cardLimitRepository.save(new CardLimit(transaction.getNumber(), 200, 1500, 1501));
        transactionRepository.save(transaction);
        LocalDateTime prevDate = transaction.getDate().minusHours(1);
        List<Transaction> byDateBetween = transactionRepository.findAllByNumberAndDateBetween(transaction.getNumber(), prevDate, transaction.getDate());
        long countRegion = byDateBetween.stream().map(Transaction::getRegion).distinct().count();
        long countIp = byDateBetween.stream().map(Transaction::getIp).distinct().count();
        if (countRegion > 3 || countIp > 3 || ipRepository.findAllByIp(transaction.getIp()) != null || stolenCardRepository.findAllByNumber(transaction.getNumber()) != null)
            return prohibitedTransact(transaction, countRegion, countIp);
        else if (countIp == 3 || countRegion == 3) {
            return manualProcessingTransaction(transaction, countRegion, countIp);
        } else
            return allowedTransaction(transaction);
    }

    public ResponseEntity<?> manualProcessingTransaction(Transaction transaction, long countRegion, long countIp) {
        String result = "MANUAL_PROCESSING";
        List<String> info = new ArrayList<>();
        if (countIp == 3)
            info.add("ip-correlation");
        if (countRegion == 3)
            info.add("region-correlation");
        transaction.setResult(result);
        transactionRepository.save(transaction);
        return new ResponseEntity<>(Map.of("result", result, "info", String.join(", ", info.stream().sorted().toList())), HttpStatus.OK);
    }

    public ResponseEntity<?> allowedTransaction(Transaction transaction) {
        if (transaction.getAmount() <= cardLimitRepository.findByNumber(transaction.getNumber()).getAllowed()) {
            transaction.setResult("ALLOWED");
            transactionRepository.save(transaction);
            return new ResponseEntity<>(Map.of("result", "ALLOWED", "info", "none"), HttpStatus.OK);
        } else if (transaction.getAmount() <= cardLimitRepository.findByNumber(transaction.getNumber()).getManualProcessing()) {
            transaction.setResult("MANUAL_PROCESSING");
            transactionRepository.save(transaction);
            return new ResponseEntity<>(Map.of("result", "MANUAL_PROCESSING", "info", "amount"), HttpStatus.OK);
        } else {
            transaction.setResult("PROHIBITED");
            transactionRepository.save(transaction);
            return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "amount"), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> prohibitedTransact(Transaction transaction, long countRegion, long countIp) {
        String result = "PROHIBITED";
        List<String> info = new ArrayList<>();
        if (countRegion > 3)
            info.add("region-correlation");
        if (countIp > 3)
            info.add("ip-correlation");
        if (transaction.getAmount() > cardLimitRepository.findByNumber(transaction.getNumber()).getManualProcessing())
            info.add("amount");
        if (ipRepository.findAllByIp(transaction.getIp()) != null)
            info.add("ip");
        if (stolenCardRepository.findAllByNumber(transaction.getNumber()) != null)
            info.add("card-number");
        transaction.setResult(result);
        transactionRepository.save(transaction);
        return new ResponseEntity<>(Map.of("result", result, "info", String.join(", ", info.stream().sorted().toList())), HttpStatus.OK);
    }

    public ResponseEntity<?> updateFeedback(FeedBackDTO feedBackDTO) {
        if (transactionRepository.findById(feedBackDTO.getTransactionId()) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!feedback.contains(feedBackDTO.getFeedback()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (transactionRepository.findById(feedBackDTO.getTransactionId()).getResult().equals(feedBackDTO.getFeedback()))
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        if (!transactionRepository.findById(feedBackDTO.getTransactionId()).getFeedback().equals(""))
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else {
            Transaction transaction = transactionRepository.findById(feedBackDTO.getTransactionId());
            CardLimit cardLimit = cardLimitRepository.findByNumber(transaction.getNumber());
            transaction.setFeedback(feedBackDTO.getFeedback());
            transactionRepository.save(transaction);
            cardLimitRepository.save(updateLimit(cardLimit, transaction, feedBackDTO));
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }
    }

    public CardLimit updateLimit(CardLimit cardLimit, Transaction transaction, FeedBackDTO feedBackDTO) {
        if (feedBackDTO.getFeedback().equals("ALLOWED") && transaction.getResult().equals("MANUAL_PROCESSING")) {
            cardLimit.setAllowed((long) Math.ceil((0.8 * cardLimit.getAllowed() + 0.2 * transaction.getAmount())));
        }
        if (feedBackDTO.getFeedback().equals("ALLOWED") && transaction.getResult().equals("PROHIBITED")) {
            cardLimit.setAllowed((long) Math.ceil((0.8 * cardLimit.getAllowed() + 0.2 * transaction.getAmount())));
            cardLimit.setManualProcessing((long) Math.ceil((0.8 * cardLimit.getManualProcessing() + 0.2 * transaction.getAmount())));
        }
        if (feedBackDTO.getFeedback().equals("MANUAL_PROCESSING") && transaction.getResult().equals("ALLOWED")) {
            cardLimit.setAllowed((long) Math.ceil((0.8 * cardLimit.getAllowed() - 0.2 * transaction.getAmount())));
        }
        if (feedBackDTO.getFeedback().equals("MANUAL_PROCESSING") && transaction.getResult().equals("PROHIBITED")) {
            cardLimit.setManualProcessing((long) Math.ceil((0.8 * cardLimit.getManualProcessing() + 0.2 * transaction.getAmount())));
        }
        if (feedBackDTO.getFeedback().equals("PROHIBITED") && transaction.getResult().equals("ALLOWED")) {
            cardLimit.setAllowed((long) Math.ceil((0.8 * cardLimit.getAllowed() - 0.2 * transaction.getAmount())));
            cardLimit.setManualProcessing((long) Math.ceil((0.8 * cardLimit.getManualProcessing() - 0.2 * transaction.getAmount())));
        }
        if (feedBackDTO.getFeedback().equals("PROHIBITED") && transaction.getResult().equals("MANUAL_PROCESSING")) {
            cardLimit.setManualProcessing((long) Math.ceil((0.8 * cardLimit.getManualProcessing() - 0.2 * transaction.getAmount())));
        }
        return cardLimit;
    }

    public ResponseEntity<?> getAllTransactionByCardNumber(String number) {
        if (!isValidCreditCardNumberLuhn(number))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (transactionRepository.findAllByNumber(number).isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(transactionRepository.findAllByNumber(number), HttpStatus.OK);
    }

    public ResponseEntity<?> getAllTransaction() {
        return new ResponseEntity<>(transactionRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    public static boolean isValidCreditCardNumberLuhn(String creditCardNumber) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(creditCardNumber);
    }
}
