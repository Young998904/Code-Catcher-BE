package com.lion.codecatcherbe.infra.kakao;

import com.lion.codecatcherbe.domain.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenProvider {
    private static String secret;
    private final Long tokenValidityInSeconds;

    public TokenProvider(@Value("${jwt.secret}") String secret,
        @Value("${jwt.token-validity-in-seconds}") Long tokenValidityInSeconds) {
        TokenProvider.secret = secret;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    public static String getSubject(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Decoders.BASE64.decode(secret))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    public String createToken(User user) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityInSeconds);

        return Jwts.builder()
            .setSubject(user.getId())
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
    }
}
