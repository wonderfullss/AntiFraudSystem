package antifraud.Service;

import antifraud.Entity.AddRoleDTO;
import antifraud.Entity.LockUnlockUserDTO;
import antifraud.Entity.Role;
import antifraud.Entity.User;
import antifraud.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public ResponseEntity<?> changeUserRole(AddRoleDTO addRoleDTO) {
        if (userRepository.findUserByUsername(addRoleDTO.getUsername()) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (addRoleDTO.getRole().equals("ADMINISTRATOR"))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        User user = userRepository.findUserByUsername(addRoleDTO.getUsername());
        if (Objects.equals(user.getRole().getAuthority(), addRoleDTO.getRole()))
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        user.setRole(Role.valueOf(addRoleDTO.getRole()));
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<?> lockUnlockUserByUsername(LockUnlockUserDTO lockUnlockUserDTO) {
        if (userRepository.findUserByUsername(lockUnlockUserDTO.getUsername()) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (Objects.equals(userRepository.findUserByUsername(lockUnlockUserDTO.getUsername()).getRole().getAuthority(), "ADMINISTRATOR"))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        User user = userRepository.findUserByUsername(lockUnlockUserDTO.getUsername());
        if (lockUnlockUserDTO.getOperation().equals("LOCK")) {
            user.setAccountNonLocked(false);
            userRepository.save(user);
            String status = String.format("User %s locked!", lockUnlockUserDTO.getUsername());
            return new ResponseEntity<>(Map.of("status", status), HttpStatus.OK);
        } else {
            user.setAccountNonLocked(true);
            userRepository.save(user);
            String status = String.format("User %s unlocked!", lockUnlockUserDTO.getUsername());
            return new ResponseEntity<>(Map.of("status", status), HttpStatus.OK);
        }
    }
}
