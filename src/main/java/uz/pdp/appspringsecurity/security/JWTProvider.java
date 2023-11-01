package uz.pdp.appspringsecurity.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class JWTProvider {
    @Value(value = "${app.jwt.access.token.key}")
    private String accessTokenKey;

    @Value(value = "${app.jwt.access.token.expiration.time}")
    private long accessTokenExpirationTime;

    @Value(value = "${app.jwt.refresh.token.key}")
    private String refreshTokenKey;

    @Value(value = "${app.jwt.refresh.token.expiration.time}")
    private long refreshTokenExpirationTime;

    public String generateAccessToken(CurrentUserDetails user) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, accessTokenKey)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .addClaims(new HashMap<>() {{
                    put("ismi", user.getName());
                    put("test", "qalay");
                }})
                .compact();
    }

    public String generateRefreshToken(CurrentUserDetails user) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, refreshTokenKey)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .compact();
    }

    public String getSubjectFromAccessToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(accessTokenKey)
                .parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }

    public String getSubjectFromRefreshToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(refreshTokenKey)
                .parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }

    public boolean accessTokenExpired(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(accessTokenKey)
                    .parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean refreshTokenExpired(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(refreshTokenKey)
                    .parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
