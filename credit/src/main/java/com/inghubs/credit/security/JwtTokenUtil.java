package com.inghubs.credit.security;

import com.inghubs.credit.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    // 1Gün
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60 * 1000;
    public static final String SIGNING_KEY = "sulek";
    public static final String USERNAME = "sub";
    public static final String USER_ROLE = "userRole";
    public static final String BEARER = "Bearer";

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(User user) {
        return doGenerateToken(user.getUsername(), user.getUserRole());
    }

    private String doGenerateToken(String subject, String role) {

        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("userRole", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("http://localhost:8181")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token));
    }

    public static String parseUserNameFromJwt(String jwt) {

        Jws<Claims> parsedToken = Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(jwt.replace(BEARER, ""));
        return parsedToken.getBody().get(USERNAME, String.class);
    }

    public static String parseUserRoleFromJwt(String jwt) {

        Jws<Claims> parsedToken = Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(jwt.replace(BEARER, ""));
        return parsedToken.getBody().get(USER_ROLE, String.class);
    }

}
