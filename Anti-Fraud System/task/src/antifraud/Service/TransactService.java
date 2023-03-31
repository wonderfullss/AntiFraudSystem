package antifraud.Service;

import antifraud.Entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactService {
    public ResponseEntity<?> Transact(Transaction transaction) {
        if (transaction.getAmount() <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else if (transaction.getAmount() <= 200) {
            return new ResponseEntity<>(Map.of("result", "ALLOWED"), HttpStatus.OK);
        } else if (transaction.getAmount() <= 1500) {
            return new ResponseEntity<>(Map.of("result", "MANUAL_PROCESSING"), HttpStatus.OK);
        } else
            return new ResponseEntity<>(Map.of("result", "PROHIBITED"), HttpStatus.OK);
    }
}
