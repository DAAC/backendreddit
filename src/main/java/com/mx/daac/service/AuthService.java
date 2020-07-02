package com.mx.daac.service;

import com.mx.daac.dto.RegisterRequest;
import com.mx.daac.exceptions.SpringRedditException;
import com.mx.daac.model.NotificationEmail;
import com.mx.daac.model.Users;
import com.mx.daac.model.VerificationToken;
import com.mx.daac.repository.UserRepository;
import com.mx.daac.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
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

    @Transactional
    public void signUp(RegisterRequest registerRequest){
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
        Users users = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name -" + username));
        users.setEnabled(true);
        userRepository.save(users);
    }
}
