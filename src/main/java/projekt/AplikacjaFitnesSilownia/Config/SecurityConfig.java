package projekt.AplikacjaFitnesSilownia.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Wyłączamy ochronę CSRF (niezbędne, aby Postman i React mogły wysyłać zapytania POST)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Konfigurujemy, kto ma dostęp do jakich adresów
                .authorizeHttpRequests(auth -> auth
                        // Zezwalamy wszystkim (np. bramce na siłowni) na uderzanie w ten jeden konkretny adres
                        .requestMatchers("/api/access/checkin").permitAll()

                        // Wszystkie inne adresy będą wymagały logowania (przydatne później)
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}