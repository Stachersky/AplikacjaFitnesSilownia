package projekt.AplikacjaFitnesSilownia.Controller;

import projekt.AplikacjaFitnesSilownia.Model.Karnet;
import projekt.AplikacjaFitnesSilownia.Model.LoginDto;
import projekt.AplikacjaFitnesSilownia.Model.RegisterDto;
import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import projekt.AplikacjaFitnesSilownia.Repository.KarnetRepository;
import projekt.AplikacjaFitnesSilownia.Repository.UzytkownikRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UzytkownikRepository uzytkownikRepo;
    private final KarnetRepository karnetRepo; // DODANE: Dostęp do bazy karnetów

    // DODANE: Wstrzykujemy KarnetRepository przez konstruktor
    public AuthController(UzytkownikRepository uzytkownikRepo, KarnetRepository karnetRepo) {
        this.uzytkownikRepo = uzytkownikRepo;
        this.karnetRepo = karnetRepo;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto dto) {
        Uzytkownik nowyUzytkownik = new Uzytkownik();
        nowyUzytkownik.setImie(dto.getImie());
        nowyUzytkownik.setNazwisko(dto.getNazwisko());
        nowyUzytkownik.setEmail(dto.getEmail());
        nowyUzytkownik.setHaslo(dto.getHaslo());
        nowyUzytkownik.setRola("ROLE_MEMBER");

        uzytkownikRepo.save(nowyUzytkownik);

        return ResponseEntity.ok("Zarejestrowano pomyślnie użytkownika: " + dto.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto dto) {
        Uzytkownik uzytkownik = uzytkownikRepo.findByEmail(dto.getEmail());

        if (uzytkownik == null || !uzytkownik.getHaslo().equals(dto.getHaslo())) {
            return ResponseEntity.status(401).body("❌ Błędny e-mail lub hasło!");
        }

        // --- NOWA LOGIKA: Sprawdzanie ważności karnetu ---
        boolean maAktywnyKarnet = false;
        Optional<Karnet> karnetOpt = karnetRepo.findByUzytkownikId(uzytkownik.getId());

        if (karnetOpt.isPresent()) {
            Karnet k = karnetOpt.get();
            // Karnet jest aktywny jeśli status to ACTIVE i data zakończenia nie jest w przeszłości
            if ("ACTIVE".equals(k.getStatus()) && !k.getDataZakonczenia().isBefore(LocalDate.now())) {
                maAktywnyKarnet = true;
            }
        }

        // Zamiast wysyłać całą encję, wysyłamy spersonalizowaną paczkę (Map)
        Map<String, Object> odpowiedz = new HashMap<>();
        odpowiedz.put("id", uzytkownik.getId());
        odpowiedz.put("rola", uzytkownik.getRola());
        odpowiedz.put("imie", uzytkownik.getImie());
        odpowiedz.put("maAktywnyKarnet", maAktywnyKarnet); // Przekazujemy kluczową informację do Reacta

        return ResponseEntity.ok(odpowiedz);
    }
}