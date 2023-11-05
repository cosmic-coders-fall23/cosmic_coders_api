package org.cosmiccoders.api.dto;

import org.cosmiccoders.api.model.HighScore;
import org.cosmiccoders.api.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HighScoreRepository extends JpaRepository<HighScore, Long> {
    HighScore findByUser(UserEntity user);

    List<HighScore> findTop10ByOrderByScoreDesc();
}
