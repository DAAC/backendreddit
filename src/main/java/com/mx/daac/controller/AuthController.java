package com.mx.daac.controller;

import com.mx.daac.dto.AuthenticationResponse;
import com.mx.daac.dto.RegisterRequest;
import com.mx.daac.dto.LoginRequest;
import com.mx.daac.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody RegisterRequest registerRequest){
        authService.signUp(registerRequest);
        return new ResponseEntity<String>("User registration successful",
                HttpStatus.OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> getAccountVerification(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<String>("Account has been activited successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);

    }

}
