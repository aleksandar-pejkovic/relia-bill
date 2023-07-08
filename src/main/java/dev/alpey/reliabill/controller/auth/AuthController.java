package dev.alpey.reliabill.controller.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.service.LoginDataService;
import dev.alpey.reliabill.service.auth.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LoginDataService loginDataService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> token(Authentication authentication) {
        String token = tokenService.generateToken(authentication);
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> dataMap = loginDataService.loadUsersData(authentication);
        dataMap.put("token", token);
        return ResponseEntity.ok().body(dataMap);
    }
}
