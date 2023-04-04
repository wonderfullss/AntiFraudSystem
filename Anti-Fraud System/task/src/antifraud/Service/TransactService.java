package antifraud.Service;

import antifraud.Entity.Transaction;
import antifraud.Repository.IpRepository;
import antifraud.Repository.StolenCardRepository;
import antifraud.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
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

    private final TransactionRepository transactionRepository;

    private final IpRepository ipRepository;

    private final StolenCardRepository stolenCardRepository;

    public ResponseEntity<?> Transact(Transaction transaction) {
        if (!isValidDate(transaction.getDate()) || transaction.getAmount() <= 0 || !isValidCreditCardNumberLuhn(transaction.getNumber()) || !regions.contains(transaction.getRegion()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        transactionRepository.save(transaction);
        Date prevDate = new Date(transaction.getDate().getTime() - 3600_000L);
        List<Transaction> byDateBetween = transactionRepository.findByDateBetween(prevDate, transaction.getDate());
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
        return new ResponseEntity<>(Map.of("result", result, "info", String.join(", ", info.stream().sorted().toList())), HttpStatus.OK);
    }

    public ResponseEntity<?> allowedTransaction(Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            return new ResponseEntity<>(Map.of("result", "ALLOWED", "info", "none"), HttpStatus.OK);
        } else if (transaction.getAmount() <= 1500) {
            return new ResponseEntity<>(Map.of("result", "MANUAL_PROCESSING", "info", "amount"), HttpStatus.OK);
        } else {
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
        if (transaction.getAmount() > 1500)
            info.add("amount");
        if (ipRepository.findAllByIp(transaction.getIp()) != null)
            info.add("ip");
        if (stolenCardRepository.findAllByNumber(transaction.getNumber()) != null)
            info.add("card-number");
        return new ResponseEntity<>(Map.of("result", result, "info", String.join(", ", info.stream().sorted().toList())), HttpStatus.OK);
    }

    public static boolean isValidCreditCardNumberLuhn(String creditCardNumber) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(creditCardNumber);
    }

    public boolean isValidDate(Date date) {
        if (date == null) return false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date.toInstant().atOffset(ZoneOffset.UTC).format(formatter);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
