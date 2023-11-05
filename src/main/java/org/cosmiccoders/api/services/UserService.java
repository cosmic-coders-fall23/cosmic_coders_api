package org.cosmiccoders.api.services;

import org.cosmiccoders.api.dto.HighScoreDto;
import org.cosmiccoders.api.dto.RegistrationDto;
import org.cosmiccoders.api.model.RefreshToken;
import org.cosmiccoders.api.model.UserEntity;

public interface UserService {
    UserEntity saveUser(RegistrationDto registrationDto);
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
    UserEntity findById(Long id);
    RefreshToken generateAndSaveRefreshToken(String email);
    String confirmVerification(String token);
    String resendVerification(String email);
    void deleteRefreshToken(Long tokenIdFromRefreshToken);
    boolean refreshTokenExistsById(Long tokenIdFromRefreshToken);
}
