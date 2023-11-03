package org.cosmiccoders.api.repository;

import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    List<VerificationToken> findAllByUser(UserEntity userEntity);
}
