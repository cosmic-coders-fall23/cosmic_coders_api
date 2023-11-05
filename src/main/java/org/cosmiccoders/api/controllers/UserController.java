package org.cosmiccoders.api.controllers;

import lombok.AllArgsConstructor;
import org.cosmiccoders.api.dto.HighScoreDto;
import org.cosmiccoders.api.dto.MessageDto;
import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.services.GameService;
import org.cosmiccoders.api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final GameService gameService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> me(Principal principal) {
        String username = principal.getName();
        UserEntity user = userService.findByUsername(username);
        return ResponseEntity.ok(new MessageDto(user.getUsername()));
    }

    @PostMapping("/addHighScore")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addHighScore(@RequestBody HighScoreDto highScoreDto, Principal principal) {
        String username = principal.getName();
        String message = gameService.addHighScore(highScoreDto, username);
        return ResponseEntity.ok(new MessageDto(message));
    }
}
