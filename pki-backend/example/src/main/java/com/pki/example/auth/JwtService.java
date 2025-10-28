package com.pki.example.auth;

import com.pki.example.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "sXiTnWpnrLpplehgXQGcCHaFKuCBhAMeUxVQECpnWRgquUWzG0mb4ptfW43CEyNa";
    private final SessionRegistry sessionRegistry;

    public JwtService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, String sessionId) {
        //Claimovi sa dodatnim podacima
        Map<String, Object> claims = new HashMap<>();

        if(userDetails instanceof User){
            User user = (User) userDetails;
            claims.put("role", user.getRole());
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail()); // OBAVEZNO
        }
        // dodavanje jwt id kao jedinstvenog identifikatora tokena
        claims.put("jti", sessionId);
        return generateToken(userDetails, claims);
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final Claims claims = extractAllClaims(token);
        final String sessionId = claims.get("jti", String.class);

        // provjera da li korisnik odgovara
        if(!username.equals(userDetails.getUsername())){
            return false;
        }

        // provjera da li je token istekao
        if(isTokenExpired(token)){
            return false;
        }

        //provjera da li postoji sesija i da li je aktivna
        SessionInfo sessionInfo = sessionRegistry.getSession(sessionId);
        if(sessionInfo == null){
            return false;
        }

        // provjera da li se userId iz tokena i sesije poklapaju
        long userIdFromToken = parseUserId(claims.get("userId"));
        if(!sessionInfo.getUserId().equals(userIdFromToken)){
            return false;
        }
        return true;
    }

    private long parseUserId(Object userIdClaim) {
        if (userIdClaim instanceof Integer) {
            return ((Integer) userIdClaim).longValue();
        } else if (userIdClaim instanceof Long) {
            return (Long) userIdClaim;
        } else {
            return Long.parseLong(userIdClaim.toString());
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
