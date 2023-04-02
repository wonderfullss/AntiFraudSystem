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

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactService {

    private final TransactionRepository transactionRepository;

    private final IpRepository ipRepository;

    private final StolenCardRepository stolenCardRepository;

    public ResponseEntity<?> Transact(Transaction transaction) {
        if (transaction.getAmount() <= 0 || !isValidCreditCardNumberLuhn(transaction.getNumber()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ipRepository.findAllByIp(transaction.getIp()) != null || stolenCardRepository.findAllByNumber(transaction.getNumber()) != null)
            return prohibitedTransact(transaction);
        else
            return allowedTransaction(transaction);
    }

    public ResponseEntity<?> allowedTransaction(Transaction transaction) {
        if (transaction.getAmount() <= 200) {
            transactionRepository.save(transaction);
            return new ResponseEntity<>(Map.of("result", "ALLOWED", "info", "none"), HttpStatus.OK);
        } else if (transaction.getAmount() <= 1500) {
            return new ResponseEntity<>(Map.of("result", "MANUAL_PROCESSING", "info", "amount"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "amount"), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> prohibitedTransact(Transaction transaction) {
        transactionRepository.save(transaction);
        if (ipRepository.findAllByIp(transaction.getIp()) != null && stolenCardRepository.findAllByNumber(transaction.getNumber()) != null)
            if (transaction.getAmount() <= 1500)
                return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "card-number, ip"), HttpStatus.OK);
            else
                return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "amount, card-number, ip"), HttpStatus.OK);
        if (ipRepository.findAllByIp(transaction.getIp()) != null)
            if (transaction.getAmount() <= 1500)
                return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "ip"), HttpStatus.OK);
            else
                return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "amount, ip"), HttpStatus.OK);
        if (stolenCardRepository.findAllByNumber(transaction.getNumber()) != null)
            if (transaction.getAmount() <= 1500)
                return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "card-number"), HttpStatus.OK);
            else
                return new ResponseEntity<>(Map.of("result", "PROHIBITED", "info", "amount, card-number"), HttpStatus.OK);
        return null;
    }

    public static boolean isValidCreditCardNumberLuhn(String creditCardNumber) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(creditCardNumber);
    }
}
