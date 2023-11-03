package org.cosmiccoders.api.services;

import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.model.VerificationToken;

import java.util.List;

public interface VerificationService {
    void save(VerificationToken verificationToken);
    VerificationToken findByToken(String token);
    void confirm(String token);
    List<VerificationToken> findAllByUser(UserEntity userEntity);
    void delete(VerificationToken verificationToken);
}
