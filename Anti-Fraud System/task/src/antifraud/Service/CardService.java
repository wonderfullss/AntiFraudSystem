package antifraud.Service;

import antifraud.Entity.StolenCard;
import antifraud.Repository.StolenCardRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardService {

    private final StolenCardRepository stolenCardRepository;

    public ResponseEntity<?> addCard(StolenCard stolenCard) {
        if (!isValidCreditCardNumberLuhn(stolenCard.getNumber()))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (stolenCardRepository.findAllByNumber(stolenCard.getNumber()) != null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        stolenCardRepository.save(stolenCard);
        return new ResponseEntity<>(stolenCard, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllCard() {
        return new ResponseEntity<>(stolenCardRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteCard(String number) {
        if (!isValidCreditCardNumberLuhn(number))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (stolenCardRepository.findAllByNumber(number) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        stolenCardRepository.deleteAllByNumber(number);
        return new ResponseEntity<>(Map.of("status", String.format("Card %s successfully removed!", number)), HttpStatus.OK);
    }

    public static boolean isValidCreditCardNumberLuhn(String creditCardNumber) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(creditCardNumber);
    }
}
