package org.cosmiccoders.api.repository;

import org.cosmiccoders.api.model.RefreshToken;
import org.cosmiccoders.api.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findAllByUser(UserEntity userEntity);
}
