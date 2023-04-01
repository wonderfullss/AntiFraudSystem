package antifraud.Сontroller;

import antifraud.Entity.AddRoleDTO;
import antifraud.Entity.LockUnlockUserDTO;
import antifraud.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/api/auth/role")
    public ResponseEntity<?> addRole(@RequestBody AddRoleDTO addRoleDTO) {
        return adminService.changeUserRole(addRoleDTO);
    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<?> lockUnlockUser(@RequestBody LockUnlockUserDTO lockUnlockUserDTO) {
        return adminService.lockUnlockUserByUsername(lockUnlockUserDTO);
    }
}
