package antifraud.Service;

import antifraud.Entity.User;
import antifraud.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;

    public ResponseEntity<?> registerUser(User user) {
        if (userRepository.findUserByUsername(user.getUsername()) != null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        else {
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
    }

    public ResponseEntity<?> getListUser() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteUserByUserName(String username) {
        if (userRepository.findUserByUsername(username) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else {
            userRepository.deleteUserByUsername(username);
            return new ResponseEntity<>(Map.of("username", username, "status", "Deleted successfully!"), HttpStatus.OK);
        }
    }
}
