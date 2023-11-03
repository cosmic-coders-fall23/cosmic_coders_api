package org.cosmiccoders.api.services.implementation;

import lombok.AllArgsConstructor;
import org.cosmiccoders.api.dto.RegistrationDto;
import org.cosmiccoders.api.model.*;
import org.cosmiccoders.api.repository.RefreshTokenRepository;
import org.cosmiccoders.api.repository.UserEntityRepository;
import org.cosmiccoders.api.security.SecurityConstants;
import org.cosmiccoders.api.services.UserService;
import org.cosmiccoders.api.services.VerificationService;
import org.cosmiccoders.api.util.EmailAPI;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userRepository;
    private final VerificationService verificationService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final EmailAPI emailAPI;

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity findById(Long id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        return userEntity.orElse(null);
    }

    public UserEntity saveUser(RegistrationDto registrationDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registrationDto.getUsername());
        userEntity.setEmail(registrationDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        Role role = roleRepository.findByName("ROLE_USER");
        userEntity.setRoles(Collections.singletonList(role));

        userRepository.save(userEntity);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(userEntity);
        verificationToken.setCreatedAt(LocalDateTime.now());
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        verificationService.save(verificationToken);

        String link = SecurityConstants.BACKEND_URL + "/auth/verify?token=" + token;
//        emailSender.send(userEntity.getEmail(), buildEmail(userEntity.getUsername(), link));
        emailAPI.send(registrationDto.getEmail(), registrationDto.getUsername(), link);

        return userEntity;
    }

    public RefreshToken generateAndSaveRefreshToken(String email) {
        UserEntity userEntity = findByEmail(email);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userEntity);
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public String confirmVerification(String token) {
        VerificationToken verificationToken = verificationService.findByToken(token);
        if (verificationToken == null) return "Invalid token";
        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) return "Token expired";
        if (verificationToken.getConfirmedAt() != null) return "Token already confirmed";

        verificationService.confirm(token);
        UserEntity userEntity = verificationToken.getUser();
        userEntity.setVerified(true);
        userRepository.save(userEntity);

        List<VerificationToken> oldTokens = verificationService.findAllByUser(userEntity);
        if (oldTokens.size() > 0) {
            for (VerificationToken oldToken : oldTokens) {
                verificationService.delete(oldToken);
            }
        }

        return "Account verified successfully";
    }

    public String resendVerification(String email) {
        UserEntity userEntity = findByEmail(email);
        if (userEntity == null) return "User not found";
        if (userEntity.isVerified()) return "User already verified";

        List<VerificationToken> oldTokens = verificationService.findAllByUser(userEntity);
        if (oldTokens.size() > 0) {
            for (VerificationToken token : oldTokens) {
                int minutes = (int) ChronoUnit.MINUTES.between(token.getCreatedAt(), LocalDateTime.now());
                if (minutes < 5) {
                    int timeToWait = 5 - minutes;
                    return "Please wait " + timeToWait + " more minutes before resending verification email";
                }
            }
        }

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(userEntity);
        verificationToken.setCreatedAt(LocalDateTime.now());
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        verificationService.save(verificationToken);

        String link = SecurityConstants.BACKEND_URL + "/auth/verify?token=" + token;
//        emailSender.send(userEntity.getEmail(), buildEmail(userEntity.getUsername(), link));
        emailAPI.send(email, userEntity.getUsername(), link);

        return "Verification email sent";
    }

    public void deleteRefreshToken(Long tokenIdFromRefreshToken) {
        refreshTokenRepository.deleteById(tokenIdFromRefreshToken);
    }

    public boolean refreshTokenExistsById(Long refreshTokenId) {
        return refreshTokenRepository.existsById(refreshTokenId);
    }
}
