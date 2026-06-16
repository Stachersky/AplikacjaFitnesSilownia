package projekt.AplikacjaFitnesSilownia.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // 1. Generowanie tokena (to już mieliśmy)
    public String generujToken(String email, String rola, Integer id) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rola", rola)
                .claim("id", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 2. Rozszyfrowanie tokena (NOWE)
    private Claims wyciagnijWszystkieDane(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String pobierzEmail(String token) {
        return wyciagnijWszystkieDane(token).getSubject();
    }

    public String pobierzRole(String token) {
        return wyciagnijWszystkieDane(token).get("rola", String.class);
    }

    // 3. Sprawdzanie czy token jest prawdziwy i nie wygasł (NOWE)
    public boolean walidujToken(String token) {
        try {
            wyciagnijWszystkieDane(token);
            return true;
        } catch (Exception e) {
            return false; // Token podrabiany lub przeterminowany!
        }
    }
}