package projekt.AplikacjaFitnesSilownia.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Wyciągamy nagłówek "Authorization" z zapytania
        String authHeader = request.getHeader("Authorization");

        // 2. Sprawdzamy, czy w ogóle jest tam Token JWT
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Ucinamy słowo "Bearer "

            // 3. Jeśli token jest ważny, logujemy gościa wewnątrz serwera
            if (jwtService.walidujToken(token)) {
                String email = jwtService.pobierzEmail(token);
                String rola = jwtService.pobierzRole(token);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, Collections.singletonList(new SimpleGrantedAuthority(rola))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4. Puszczamy zapytanie dalej (do SecurityConfig)
        filterChain.doFilter(request, response);
    }
}