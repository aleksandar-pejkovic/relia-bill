package dev.alpey.reliabill.controller.jwt;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.service.LoginDataService;
import dev.alpey.reliabill.service.jwt.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LoginDataService loginDataService;

    @PostMapping("/login")
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> fetchUsersData(Principal principal) {
        Map<String, Object> dataMap = loginDataService.loadUsersData(principal);
        return ResponseEntity.ok().body(dataMap);
    }
}
