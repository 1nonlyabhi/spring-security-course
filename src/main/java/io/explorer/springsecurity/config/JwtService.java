package io.explorer.springsecurity.config;

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

    private static final String SECRET_KEY = "2305ba1bf857b9dc419449738a3d230f99b83538c1865e64f3153d07bd1f57cca0383c60e363ffba9734773b44e0a01cb0004af7a22580f9dc94265c3bb8242176bb3e4094b95265291169f268fa2e46a87e3f88c58201125c95d1640e21c8df5d1c9e29293610c0a76c62a163efe6c864a1e4c0c8db313bc022dd723d85cce2aab41d39657ff26bbc8d6054d429373394e0397d70a1df612fee9afc36660b0e82e9a30a2a34487a599439fb28465381f1e9d3fab86a05be6c0d5279f2ac77a32ac604991b2c7a6c11f1ffb9aad64d2a05789ec9df046130e1ce89067bfb54e477e395dff493a1028689cd52f2232b09478b703524ec46bcdb0d100b97b211a0";

     String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
         final Claims claims = extractAllClaims(token);
         return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
         return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
         return Jwts.builder()
                 .setClaims(extraClaims)
                 .setSubject(userDetails.getUsername())
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                 .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                 .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
         final String username = extractUsername(token);
         return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
         return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
         return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
         return Jwts.parserBuilder()
                 .setSigningKey(getSignInKey())
                 .build()
                 .parseClaimsJwt(token)
                 .getBody();
    }

    private Key getSignInKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
