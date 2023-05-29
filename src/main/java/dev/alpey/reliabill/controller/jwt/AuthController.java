package dev.alpey.reliabill.controller.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.alpey.reliabill.service.jwt.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;

    @CrossOrigin(origins = {"https://reliabill.netlify.app", "http://localhost:5173"})
    @PostMapping("/login")
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }
}
