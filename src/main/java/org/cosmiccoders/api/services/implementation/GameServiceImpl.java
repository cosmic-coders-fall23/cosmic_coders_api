package org.cosmiccoders.api.services.implementation;

import lombok.AllArgsConstructor;
import org.cosmiccoders.api.dto.HighScoreDto;
import org.cosmiccoders.api.dto.HighScoreRepository;
import org.cosmiccoders.api.dto.LeaderboardScore;
import org.cosmiccoders.api.model.HighScore;
import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.services.GameService;
import org.cosmiccoders.api.services.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class GameServiceImpl implements GameService {
    private final UserService userService;
    private final HighScoreRepository highScoreRepository;

    public String addHighScore(HighScoreDto highScoreDto, String username) {
        UserEntity user = userService.findByUsername(username);
        if (user == null) return "Invalid user";
        if (highScoreDto == null) return "Invalid high score";

        HighScore currentHighScore = highScoreRepository.findByUser(user);
        int score = currentHighScore.getScore();
        if (score >= highScoreDto.getHighScore()) return "Invalid high score";

        currentHighScore.setScore(highScoreDto.getHighScore());
        highScoreRepository.save(currentHighScore);
        return "Successfully added high score";
    }

    public HighScore getHighScore(UserEntity user) {
        return highScoreRepository.findByUser(user);
    }

    public List<LeaderboardScore> getLeaderboard() {
        List<HighScore> highScores = highScoreRepository.findTop10ByOrderByScoreDesc();
        List<LeaderboardScore> leaderboard = new ArrayList<>();
        for (HighScore highScore : highScores) {
            leaderboard.add(new LeaderboardScore(highScore.getId(), highScore.getUser().getUsername(), highScore.getScore()));
        }
        return leaderboard;
    }
}
