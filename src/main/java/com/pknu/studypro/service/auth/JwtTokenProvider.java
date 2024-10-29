package com.pknu.studypro.service.auth;

import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpired;
    private final long refreshTokenExpired;

    public JwtTokenProvider(@Value("${spring.auth.key}") final String key,
                            @Value("${spring.auth.accessTokenExpired}") final long accessTokenExpired,
                            @Value("${spring.auth.refreshTokenExpired}") final long refreshTokenExpired) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpired = accessTokenExpired;
        this.refreshTokenExpired = refreshTokenExpired;
    }

    public String createAccessFrom(final String memberId) {
        return builder(accessTokenExpired)
                .setSubject(memberId)
                .compact();
    }

    private JwtBuilder builder(final long expired) {
        final Date validity = new Date(System.currentTimeMillis() + expired);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256);
    }

    public String createRefresh() {
        return builder(refreshTokenExpired).compact();
    }

    public String getUsername(final String token) {
        return toClaims(token).getSubject();
    }

    private Claims toClaims(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (final ExpiredJwtException expired) {
            return expired.getClaims();
        } catch (final Exception e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    public String refreshAccessToken(final String access, final String refresh) {
        validate(refresh);
        return createAccessFrom(getUsername(access));
    }

    public void validate(final String token) {
        final Claims claims = toClaims(token);

        if (claims.getExpiration().before(new Date())) {
            throw new BusinessLogicException(ExceptionCode.TOKEN_EXPIRED);
        }
    }
}
