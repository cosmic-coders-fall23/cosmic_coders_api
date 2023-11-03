package org.cosmiccoders.api.services.implementation;

import lombok.AllArgsConstructor;
import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.model.VerificationToken;
import org.cosmiccoders.api.repository.VerificationTokenRepository;
import org.cosmiccoders.api.services.VerificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final VerificationTokenRepository verificationTokenRepository;

    public void save(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token).orElse(null);
    }

    public void confirm(String token) {
        VerificationToken verificationToken = findByToken(token);
        verificationToken.setConfirmedAt(LocalDateTime.now());
        verificationTokenRepository.save(verificationToken);
    }

    public List<VerificationToken> findAllByUser(UserEntity userEntity) {
        return verificationTokenRepository.findAllByUser(userEntity);
    }

    public void delete(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }
}
