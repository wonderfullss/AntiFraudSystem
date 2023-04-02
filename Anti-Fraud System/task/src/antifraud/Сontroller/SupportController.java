package antifraud.Сontroller;

import antifraud.Entity.IpAddress;
import antifraud.Entity.StolenCard;
import antifraud.Service.CardService;
import antifraud.Service.IpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final IpService ipService;

    private final CardService cardService;

    @PostMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<?> addIp(@RequestBody @Valid IpAddress ipAddress) {
        return ipService.addIpAddress(ipAddress);
    }

    @GetMapping("/api/antifraud/suspicious-ip")
    public ResponseEntity<?> getIp() {
        return ipService.getAllIpAddress();
    }

    @DeleteMapping("/api/antifraud/suspicious-ip/{ip}")
    public ResponseEntity<?> deleteIp(@PathVariable String ip) {
        return ipService.deleteIpAddress(ip);
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<?> addCard(@RequestBody @Valid StolenCard stolenCard) {
        return cardService.addCard(stolenCard);
    }

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<?> getCards() {
        return cardService.getAllCard();
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public ResponseEntity<?> deleteCard(@PathVariable String number) {
        return cardService.deleteCard(number);
    }
}
