package dev.alpey.reliabill.controller.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.service.password.PasswordResetService;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email) {
        passwordResetService.resetPassword(email);
        return new ResponseEntity<>("Password reset token was sent!", HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updatePassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword
    ) {
        passwordResetService.updatePassword(token, newPassword);
        return new ResponseEntity<>("Password updated!", HttpStatus.OK);
    }
}
