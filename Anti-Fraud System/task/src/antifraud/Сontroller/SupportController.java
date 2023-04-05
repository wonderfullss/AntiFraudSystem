package antifraud.Сontroller;

import antifraud.Entity.FeedBackDTO;
import antifraud.Entity.IpAddress;
import antifraud.Entity.StolenCard;
import antifraud.Service.CardService;
import antifraud.Service.IpService;
import antifraud.Service.TransactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final IpService ipService;

    private final CardService cardService;

    private final TransactService transactService;

    @PutMapping("/api/antifraud/transaction")
    public ResponseEntity<?> updateFeedback(@RequestBody FeedBackDTO feedBackDTO) {
        return transactService.updateFeedback(feedBackDTO);
    }

    @GetMapping("/api/antifraud/history")
    public ResponseEntity<?> getTransact() {
        return transactService.getAllTransaction();
    }

    @GetMapping("/api/antifraud/history/{number}")
    public ResponseEntity<?> getTransactByCard(@PathVariable String number) {
        return transactService.getAllTransactionByCardNumber(number);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<?> getIp() {
        return ipService.getAllIpAddress();
    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<?> getCards() {
        return cardService.getAllCard();
    }

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<?> addIp(@RequestBody @Valid IpAddress ipAddress) {
        return ipService.addIpAddress(ipAddress);
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<?> addCard(@RequestBody @Valid StolenCard stolenCard) {
        return cardService.addCard(stolenCard);
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<?> deleteIp(@PathVariable String ip) {
        return ipService.deleteIpAddress(ip);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<?> deleteCard(@PathVariable String number) {
        return cardService.deleteCard(number);
    }
}
