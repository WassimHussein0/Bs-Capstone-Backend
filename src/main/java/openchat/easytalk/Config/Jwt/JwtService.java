package openchat.easytalk.Config.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import openchat.easytalk.User.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {


    private static final String SECRET_KEY = "IE2wFVrWj54a8tk7R7vyrpjXcJ6UlompPh/5bHXKQkA";
    private final static long EXPIRATION = 1000 * 60 * 60 * 24; // 24 hour

    public static String getToken() {

        return ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes())
                .getRequest()
                .getHeader("Authorization").substring(7);
    }

    public static Map<String, Object> extraClaim(User user) {
        Map<String, Object> extraClaim = new HashMap<>();

        extraClaim.put("id", user.getId());
        extraClaim.put("role", user.getRole().name());
        extraClaim.put("joinDate", user.getJoinDate());
        return extraClaim;
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static String extractUsername() {
        return extractClaim(getToken(), Claims::getSubject);
    }


    public static String toString(String token) {
        return extractClaim(token, Claims::toString);
    }

    public static Object extractGivenClaim(String claimName) {
        final Claims claims = extractAllClaims(getToken());
        return claims.get(claimName, Object.class);
    }


    public static <T> T extractClaim(String token, Function<Claims, T> claimsFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(User userDetails) {
        return generateToken(extraClaim(userDetails), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaim, UserDetails userDetails) {

        return Jwts
                .builder()
                .setClaims(extraClaim)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

}
