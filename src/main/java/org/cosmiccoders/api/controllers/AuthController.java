package org.cosmiccoders.api.controllers;

import lombok.AllArgsConstructor;
import org.cosmiccoders.api.dto.*;
import org.cosmiccoders.api.model.HighScore;
import org.cosmiccoders.api.model.RefreshToken;
import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.security.JwtTokenUtil;
import org.cosmiccoders.api.security.SecurityConstants;
import org.cosmiccoders.api.services.GameService;
import org.cosmiccoders.api.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final GameService gameService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDto registrationDto) {
        UserEntity existingUserEmail = userService.findByEmail(registrationDto.getEmail());
        if (existingUserEmail != null && existingUserEmail.getEmail() != null && !existingUserEmail.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageDto("Email already exists"));
        }

        UserEntity existingUsername = userService.findByUsername(registrationDto.getUsername());
        if (existingUsername != null && existingUsername.getUsername() != null && !existingUsername.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageDto("Username already exists"));
        }
        UserEntity user = userService.saveUser(registrationDto);

        return ResponseEntity.ok(new MessageDto("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user = userService.findByUsername(authentication.getName());
        if(!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(new MessageDto("User not verified"));
        }
        HighScore highScore = gameService.getHighScore(user);

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        RefreshToken refreshToken = userService.generateAndSaveRefreshToken(loginDto.getEmail());
        String refreshTokenString = jwtTokenUtil.generateRefreshToken(user, refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshTokenString)
                .domain(SecurityConstants.COOKIE_DOMAIN)
                .httpOnly(true)
                .secure(SecurityConstants.IS_COOKIE_SECURE)
                .path("/")
                .maxAge(SecurityConstants.REFRESH_EXPIRATION_SECONDS)
                .sameSite(SecurityConstants.COOKIE_SAME_SITE)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .domain(SecurityConstants.COOKIE_DOMAIN)
                .httpOnly(true)
                .secure(SecurityConstants.IS_COOKIE_SECURE)
                .path("/")
                .maxAge(SecurityConstants.JWT_EXPIRATION_SECONDS)
                .sameSite(SecurityConstants.COOKIE_SAME_SITE)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(new AuthenticationResponseDto(user.getUsername(), user.getEmail(), highScore.getScore()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie accessToken = ResponseCookie.from("accessToken", "")
                .domain(SecurityConstants.COOKIE_DOMAIN)
                .httpOnly(true)
                .secure(SecurityConstants.IS_COOKIE_SECURE)
                .path("/")
                .maxAge(0)
                .sameSite(SecurityConstants.COOKIE_SAME_SITE)
                .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "")
                .domain(SecurityConstants.COOKIE_DOMAIN)
                .httpOnly(true)
                .secure(SecurityConstants.IS_COOKIE_SECURE)
                .path("/")
                .maxAge(0)
                .sameSite(SecurityConstants.COOKIE_SAME_SITE)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                .header(HttpHeaders.SET_COOKIE, accessToken.toString())
                .body(new MessageDto("Refresh token deleted"));
    }

    @PostMapping("/access-token")
    public ResponseEntity<?> getAccessToken(@CookieValue(name = "refreshToken", required = false) String token) {
        if(token == null) return ResponseEntity.badRequest().body("Missing refresh token");
        if (jwtTokenUtil.validateRefreshToken(token) && userService.refreshTokenExistsById(jwtTokenUtil.getTokenIdFromRefreshToken(token))) {
            UserEntity user = userService.findById(jwtTokenUtil.getUserIdFromRefreshToken(token));
            String accessToken = jwtTokenUtil.generateAccessToken(user);
            System.out.println("REGENERATED ACCESS TOKEN");

            ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                    .domain(SecurityConstants.COOKIE_DOMAIN)
                    .httpOnly(true)
                    .secure(SecurityConstants.IS_COOKIE_SECURE)
                    .path("/")
                    .maxAge(SecurityConstants.JWT_EXPIRATION_SECONDS)
                    .sameSite(SecurityConstants.COOKIE_SAME_SITE)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new MessageDto("access refreshed"));
        }

        return ResponseEntity.badRequest().body(new MessageDto("Refresh token is invalid"));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        String response = userService.confirmVerification(token);
        if(!response.contains("successfully")) return ResponseEntity.badRequest().location(URI.create(SecurityConstants.FRONTEND_URL + "/login")).body(new MessageDto(response));
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(SecurityConstants.FRONTEND_URL + "/verified")).body(new MessageDto(response));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody ResendVerificationDto dto) {
        String response = userService.resendVerification(dto.getEmail());
        if(!response.contains("sent")) return ResponseEntity.badRequest().body(new MessageDto(response));
        return ResponseEntity.ok(new MessageDto(response));
    }
}
