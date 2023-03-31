package antifraud.Сontroller;

import antifraud.Entity.Transaction;
import antifraud.Service.TransactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactService transactService;

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity<?> postTransact(@RequestBody Transaction transaction) {
        return transactService.Transact(transaction);
    }
}
