package antifraud.Сontroller;

import antifraud.Entity.User;
import antifraud.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/api/auth/user")
    public ResponseEntity<?> signUp(@RequestBody @Valid User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<?> getUsers() {
        return userService.getListUser();
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return userService.deleteUserByUserName(username);
    }
}
