package com.mx.daac.service;

import com.mx.daac.dto.AuthenticationResponse;
import com.mx.daac.dto.RefreshTokenRequest;
import com.mx.daac.dto.RegisterRequest;
import com.mx.daac.exceptions.SpringRedditException;
import com.mx.daac.dto.LoginRequest;
import com.mx.daac.model.NotificationEmail;
import com.mx.daac.model.Users;
import com.mx.daac.model.VerificationToken;
import com.mx.daac.repository.UserRepository;
import com.mx.daac.repository.VerificationTokenRepository;
import com.mx.daac.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signUp(RegisterRequest registerRequest) {
        Users users = new Users();
        users.setUsername(registerRequest.getUsername());
        users.setEmail(registerRequest.getEmail());
        users.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        users.setCreated(Instant.now());
        users.setEnabled(false);

        userRepository.save(users);

        String token = generateVerificationToken(users);
        mailService.sendMail(new NotificationEmail("Please activate your account",
                users.getEmail(),
                "Thank you for signing up to reddit demo," +
                        "please click on the below url to activate your account" +
                        " http://localhost:8080/api/auth/accountVerification/" + token));

    }

    private String generateVerificationToken(Users users) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(users);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new SpringRedditException("User not found with name -" + username));
        users.setEnabled(true);
        userRepository.save(users);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expireAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();

    }

    public Users getCurrentUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() ->
                        new SpringRedditException("User not found with name - " + principal.getUsername()));
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());

        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expireAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
}
