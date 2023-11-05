package org.cosmiccoders.api.controllers;

import lombok.AllArgsConstructor;
import org.cosmiccoders.api.dto.LeaderboardScore;
import org.cosmiccoders.api.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @GetMapping("/leaderboard")
    public ResponseEntity<?> leaderboard() {
        List<LeaderboardScore> leaderboard = gameService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}
