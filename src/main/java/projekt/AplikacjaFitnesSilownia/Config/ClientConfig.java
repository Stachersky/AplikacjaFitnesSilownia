package projekt.AplikacjaFitnesSilownia.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient netApiClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:5158/api")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}