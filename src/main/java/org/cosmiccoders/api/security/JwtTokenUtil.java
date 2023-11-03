package org.cosmiccoders.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.cosmiccoders.api.model.RefreshToken;
import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JwtTokenUtil {
    private final UserService userService;

    @Autowired
    public JwtTokenUtil(UserService userService) {
        this.userService = userService;
    }

    public String generateAccessToken(UserEntity user) {
        String id = user.getId().toString();
        Date currDate = new Date();
        Date expiryDate = new Date(currDate.getTime() + SecurityConstants.JWT_EXPIRATION);

        return JWT.create()
                .withIssuer(SecurityConstants.ISSUER)
                .withSubject(id)
                .withIssuedAt(currDate)
                .withExpiresAt(expiryDate)
                .sign(Algorithm.HMAC512(SecurityConstants.JWT_SECRET));
    }

    public String generateRefreshToken(UserEntity user, RefreshToken refreshToken) {
        String id = user.getId().toString();
        Date currDate = new Date();
        Date expiryDate = new Date(currDate.getTime() + SecurityConstants.REFRESH_EXPIRATION);

        return JWT.create()
                .withIssuer(SecurityConstants.ISSUER)
                .withSubject(id)
                .withIssuedAt(currDate)
                .withExpiresAt(expiryDate)
                .withClaim("tokenId", refreshToken.getId())
                .sign(Algorithm.HMAC512(SecurityConstants.REFRESH_SECRET));
    }

    private Optional<DecodedJWT> decodeAccessToken(String token) {
        JWTVerifier accessTokenVerifier = JWT.require(HMAC512(SecurityConstants.JWT_SECRET))
                .withIssuer(SecurityConstants.ISSUER)
                .build();
        try {
            return Optional.of(accessTokenVerifier.verify(token));
        } catch (JWTVerificationException e) {
            System.out.println("Access token " + token + " is invalid");
//            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<DecodedJWT> decodeRefreshToken(String token) {
        JWTVerifier refreshTokenVerifier = JWT.require(HMAC512(SecurityConstants.JWT_SECRET))
                .withIssuer(SecurityConstants.ISSUER)
                .build();
        try {
            return Optional.of(refreshTokenVerifier.verify(token));
        } catch (JWTVerificationException e) {
            System.out.println("Refresh token is invalid");
//            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean validateAccessToken(String token) {
        return decodeAccessToken(token).isPresent();
    }

    public boolean validateRefreshToken(String token) {
        return decodeRefreshToken(token).isPresent();
    }

    public String getUserIdFromAccessToken(String token) {
        return decodeAccessToken(token).get().getSubject();
    }

    public String getEmailFromAccessToken(String token) {
        UserEntity user = userService.findById(Long.valueOf(getUserIdFromAccessToken(token)));
        return user.getEmail();
    }

    public Long getUserIdFromRefreshToken(String token) {
        return Long.valueOf(decodeRefreshToken(token).get().getSubject());
    }

    public Long getTokenIdFromRefreshToken(String token) {
        return decodeRefreshToken(token).get().getClaim("tokenId").asLong();
    }
}
