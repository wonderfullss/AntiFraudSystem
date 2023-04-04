package antifraud.Service;

import antifraud.Entity.IpAddress;
import antifraud.Repository.IpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IpService {

    private final IpRepository ipRepository;

    public ResponseEntity<?> addIpAddress(IpAddress ipAddress) {
        if (ipRepository.findAllByIp(ipAddress.getIp()) != null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        ipRepository.save(ipAddress);
        return new ResponseEntity<>(ipAddress, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllIpAddress() {
        return new ResponseEntity<>(ipRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteIpAddress(@Valid String ip) {
        String regex = "^(?:(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)(?:\\.(?!$)|$)){4}$";
        if (!ip.matches(regex))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ipRepository.findAllByIp(ip) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        ipRepository.deleteAllByIp(ip);
        return new ResponseEntity<>(Map.of("status", String.format("IP %s successfully removed!", ip)), HttpStatus.OK);
    }
}
