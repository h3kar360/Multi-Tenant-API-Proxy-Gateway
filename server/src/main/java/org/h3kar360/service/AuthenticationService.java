package org.h3kar360.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.LoginInputDto;
import org.h3kar360.dto.SignUpInputDto;
import org.h3kar360.dto.VerifyClientDto;
import org.h3kar360.model.Client;
import org.h3kar360.repository.ClientRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public Client signup(SignUpInputDto signUpInputDto) {
        Client client = new Client();

        client.setClientName(signUpInputDto.getUsername());
        client.setClientEmail(signUpInputDto.getEmail());
        client.setClientPassword(passwordEncoder.encode(signUpInputDto.getPassword()));
        client.setRateLimit(100);
        client.setRateLimitWindow(1000);
        client.setActive(false);
        client.setVerificationCode(generateVerificationCode());
        client.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        //sendVerificationEmail(client);
        return clientRepository.save(client);
    }

    public Client authenticate(LoginInputDto loginInputDto) {
        Client client = clientRepository.findByClientEmail(loginInputDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!client.isActive())
            throw new RuntimeException("Account not verified. Please verify your account");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginInputDto.getEmail(),
                        loginInputDto.getPassword()
                )
        );

        return client;
    }

    public void verifyClient(VerifyClientDto verifyClientDto) {
        Optional<Client> optionalClient = clientRepository.findByClientEmail(verifyClientDto.getEmail());

        if(optionalClient.isPresent()) {
            Client client = optionalClient.get();

            if(client.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Verification code has expired");

            if(client.getVerificationCode().equals(verifyClientDto.getVerificationCode())) {
                client.setActive(true);
                client.setVerificationCode(null);
                client.setVerificationCodeExpiresAt(null);
                clientRepository.save(client);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<Client> optionalClient = clientRepository.findByClientEmail(email);

        if(optionalClient.isPresent()) {
            Client client = optionalClient.get();

            if(client.isActive())
                throw new RuntimeException("Account is already verified");

            client.setVerificationCodeExpiresAt(LocalDateTime.now());
            client.setVerificationCode(generateVerificationCode());
            sendVerificationEmail(client);
            clientRepository.save(client);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmail(Client client) {
        String subject = "Account Verification";
        String verificationCode = "Verification code: " + client.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Proxier!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(client.getClientEmail(), subject, htmlMessage);
        } catch(MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
