package projekt.AplikacjaFitnesSilownia.Service;

import projekt.AplikacjaFitnesSilownia.Model.Karnet;
import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import projekt.AplikacjaFitnesSilownia.Model.Wejscie;
import projekt.AplikacjaFitnesSilownia.Repository.KarnetRepository;
import projekt.AplikacjaFitnesSilownia.Repository.UzytkownikRepository;
import projekt.AplikacjaFitnesSilownia.Repository.WejscieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AccessService {

    private final KarnetRepository karnetRepo;
    private final WejscieRepository wejscieRepo;
    private final UzytkownikRepository uzytkownikRepo;
    private final RestClient netApiClient;

    public AccessService(KarnetRepository karnetRepo, WejscieRepository wejscieRepo,
                         UzytkownikRepository uzytkownikRepo, RestClient netApiClient) {
        this.karnetRepo = karnetRepo;
        this.wejscieRepo = wejscieRepo;
        this.uzytkownikRepo = uzytkownikRepo;
        this.netApiClient = netApiClient;
    }

    @Transactional
    public String rejestrujWejscie(Integer uzytkownikId) {
        // 1. Sprawdzamy czy w ogóle jest taki użytkownik
        Uzytkownik uzytkownik = uzytkownikRepo.findById(uzytkownikId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika!"));

        // 2. Weryfikacja karnetu
        Karnet karnet = karnetRepo.findByUzytkownikId(uzytkownikId)
                .orElseThrow(() -> new RuntimeException("Brak przypisanego karnetu!"));

        if (karnet.getDataZakonczenia().isBefore(LocalDate.now()) || !"ACTIVE".equals(karnet.getStatus())) {
            throw new RuntimeException("Karnet jest nieważny lub wygasł!");
        }

        // 3. Rejestrujemy wejście do bazy MySQL (automatycznie dzięki Hibernate)
        Wejscie wejscie = new Wejscie();
        wejscie.setUzytkownik(uzytkownik);
        wejscie.setDataGodzina(LocalDateTime.now());
        wejscieRepo.save(wejscie);

        // 4. Integracja z .NET (Tworzymy sesję treningową)
        try {
            Map<String, Object> requestBody = Map.of("uzytkownikId", uzytkownikId, "typ", "SIŁOWNIA");

            ResponseEntity<String> response = netApiClient.post()
                    .uri("/workouts/sessions/start")
                    .body(requestBody)
                    .retrieve()
                    .toEntity(String.class);

            return "Zeskanowano pomyślnie. Status sesji w .NET: " + response.getBody();
        } catch (Exception e) {
            // Jeśli C# "leży", to i tak wpuszczamy klienta (karnet działa), ale logujemy błąd
            return "Wejście zarejestrowane, ale serwer planów treningowych (.NET) nie odpowiedział.";
        }
    }
}