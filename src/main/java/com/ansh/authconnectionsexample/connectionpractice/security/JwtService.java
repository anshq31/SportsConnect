package com.ansh.authconnectionsexample.connectionpractice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh.expiration-ms}")
    private long refreshExpirationMs;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username){
        return buildToken(username,accessExpirationMs);
    }

    public String generateRefreshToken(String username){
        return buildToken(username,refreshExpirationMs);
    }

    private String buildToken(String subject , long expirationMs){
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username){
        final String subject = extractClaims(token,Claims::getSubject);
        return (subject.equals(username) && !isTokenExpired(token));
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token){
        Date expiration = extractClaims(token,Claims::getExpiration);
        return expiration.before(new Date());
    }

    public String extractUsername(String token){
        return extractClaims(token,Claims::getSubject);
    }
}
