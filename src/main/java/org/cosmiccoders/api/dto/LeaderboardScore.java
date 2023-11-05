package org.cosmiccoders.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardScore {
    private Long id;
    private String username;
    private int score;
}
