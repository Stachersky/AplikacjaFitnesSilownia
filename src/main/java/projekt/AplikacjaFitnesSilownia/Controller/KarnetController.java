package projekt.AplikacjaFitnesSilownia.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projekt.AplikacjaFitnesSilownia.Model.Karnet;
import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import projekt.AplikacjaFitnesSilownia.Repository.KarnetRepository;
import projekt.AplikacjaFitnesSilownia.Repository.UzytkownikRepository;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/karnety")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class KarnetController {

    private final KarnetRepository karnetRepository;
    private final UzytkownikRepository uzytkownikRepository;

    // Wstrzykujemy repozytoria, żeby móc czytać/pisać do bazy
    public KarnetController(KarnetRepository karnetRepository, UzytkownikRepository uzytkownikRepository) {
        this.karnetRepository = karnetRepository;
        this.uzytkownikRepository = uzytkownikRepository;
    }

    @PostMapping("/kup/{userId}")
    public ResponseEntity<String> kupKarnet(@PathVariable Integer userId, @RequestBody String nazwaKarnetu) {
        System.out.println("✅ Otrzymano płatność! Klient ID: " + userId + " kupuje: " + nazwaKarnetu);

        // 1. Szukamy klienta w bazie
        Uzytkownik uzytkownik = uzytkownikRepository.findById(userId).orElse(null);
        if (uzytkownik == null) {
            return ResponseEntity.badRequest().body("Błąd: Nie znaleziono użytkownika.");
        }

        // 2. Sprawdzamy, czy klient ma już jakiś (nawet stary) karnet w bazie
        Optional<Karnet> istniejacyKarnet = karnetRepository.findByUzytkownikId(userId);
        Karnet karnet;

        if (istniejacyKarnet.isPresent()) {
            karnet = istniejacyKarnet.get();
            // Jeśli karnet jest wygasły - odnawiamy od dziś na 30 dni
            // Jeśli jest nadal aktywny - dodajemy 30 dni do obecnej daty końca
            if (karnet.getDataZakonczenia().isBefore(LocalDate.now())) {
                karnet.setDataZakonczenia(LocalDate.now().plusDays(30));
            } else {
                karnet.setDataZakonczenia(karnet.getDataZakonczenia().plusDays(30));
            }
            karnet.setStatus("ACTIVE");
        } else {
            // 3. Jeśli to pierwszy zakup, tworzymy nowy karnet od zera
            karnet = new Karnet();
            karnet.setUzytkownik(uzytkownik);
            karnet.setDataZakonczenia(LocalDate.now().plusDays(30));
            karnet.setStatus("ACTIVE");
        }

        // 4. Zapisujemy do MySQL
        karnetRepository.save(karnet);

        return ResponseEntity.ok("Twój " + nazwaKarnetu + " został pomyślnie aktywowany! Ważny do: " + karnet.getDataZakonczenia());
    }
}