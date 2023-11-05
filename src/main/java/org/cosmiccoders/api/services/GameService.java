package org.cosmiccoders.api.services;

import org.cosmiccoders.api.dto.HighScoreDto;
import org.cosmiccoders.api.dto.LeaderboardScore;
import org.cosmiccoders.api.model.HighScore;
import org.cosmiccoders.api.model.UserEntity;

import java.util.List;

public interface GameService {
    String addHighScore(HighScoreDto highScoreDto, String username);
    HighScore getHighScore(UserEntity user);
    List<LeaderboardScore> getLeaderboard();
}
