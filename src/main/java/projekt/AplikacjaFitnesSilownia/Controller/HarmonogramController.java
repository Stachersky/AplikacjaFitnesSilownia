package projekt.AplikacjaFitnesSilownia.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projekt.AplikacjaFitnesSilownia.Model.TreningPersonalny;
import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import projekt.AplikacjaFitnesSilownia.Repository.TreningPersonalnyRepository;
import projekt.AplikacjaFitnesSilownia.Repository.UzytkownikRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/harmonogram")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class HarmonogramController {

    private final TreningPersonalnyRepository harmonogramRepo;
    private final UzytkownikRepository uzytkownikRepo;

    public HarmonogramController(TreningPersonalnyRepository harmonogramRepo, UzytkownikRepository uzytkownikRepo) {
        this.harmonogramRepo = harmonogramRepo;
        this.uzytkownikRepo = uzytkownikRepo;
    }

    // 1. Instruktor dodaje swój wolny termin
    @PostMapping("/dodaj")
    public ResponseEntity<String> dodajTermin(@RequestParam Integer trenerId, @RequestBody String dataGodzinaStr) {
        Uzytkownik trener = uzytkownikRepo.findById(trenerId).orElse(null);
        if (trener == null || !"ROLE_INSTRUCTOR".equals(trener.getRola())) {
            return ResponseEntity.status(403).body("Odmowa dostępu!");
        }

        TreningPersonalny trening = new TreningPersonalny();
        trening.setTrener(trener);

        trening.setDataGodzina(LocalDateTime.parse(dataGodzinaStr.replace("\"", "")));
        trening.setStatus("WOLNY");

        harmonogramRepo.save(trening);
        return ResponseEntity.ok("✅ Dodano wolny termin do Twojego grafiku!");
    }

    // 2. Pobieranie listy wolnych terminów dla klienta
    @GetMapping("/wolne")
    public ResponseEntity<List<TreningPersonalny>> pobierzWolneTerminy() {
        return ResponseEntity.ok(harmonogramRepo.findByStatus("WOLNY"));
    }

    // 3. Rezerwacja terminu przez klienta
    @PostMapping("/{treningId}/rezerwuj")
    public ResponseEntity<String> rezerwujTermin(@PathVariable Integer treningId, @RequestParam Integer klientId) {
        TreningPersonalny trening = harmonogramRepo.findById(treningId).orElse(null);
        Uzytkownik klient = uzytkownikRepo.findById(klientId).orElse(null);

        if (trening == null || klient == null) return ResponseEntity.badRequest().body("Błąd danych.");
        if (!"WOLNY".equals(trening.getStatus())) return ResponseEntity.badRequest().body("Termin już zajęty!");

        trening.setKlient(klient);
        trening.setStatus("ZAREZERWOWANY");
        harmonogramRepo.save(trening);

        return ResponseEntity.ok("✅ Zarezerwowano trening personalny z instruktorem: " + trening.getTrener().getImie());
    }

    // 4. Instruktor sprawdza swój grafik (z kim ma zajęcia)
    @GetMapping("/trener/{trenerId}")
    public ResponseEntity<List<TreningPersonalny>> grafikTrenera(@PathVariable Integer trenerId) {
        return ResponseEntity.ok(harmonogramRepo.findByTrenerId(trenerId));
    }
    // 5. Klient sprawdza swoje zarezerwowane treningi
    @GetMapping("/klient/{klientId}")
    public ResponseEntity<List<TreningPersonalny>> mojeTreningi(@PathVariable Integer klientId) {
        return ResponseEntity.ok(harmonogramRepo.findByKlientId(klientId));
    }
    // 6. Klient anuluje zarezerwowany trening 1 na 1 (Termin wraca do puli jako WOLNY)
    @PostMapping("/{treningId}/anuluj")
    public ResponseEntity<String> anulujTreningKlienta(@PathVariable Integer treningId) {
        TreningPersonalny trening = harmonogramRepo.findById(treningId).orElse(null);
        if (trening != null) {
            trening.setKlient(null);
            trening.setStatus("WOLNY");
            harmonogramRepo.save(trening);
            return ResponseEntity.ok("✅ Odwołano trening. Termin wraca do puli wolnych.");
        }
        return ResponseEntity.badRequest().body("❌ Błąd: Nie znaleziono treningu.");
    }

    // 7. Instruktor całkowicie usuwa termin ze swojego grafiku
    @DeleteMapping("/{treningId}/usun")
    public ResponseEntity<String> usunTerminInstruktora(@PathVariable Integer treningId) {
        harmonogramRepo.deleteById(treningId);
        return ResponseEntity.ok("✅ Usunięto termin ze swojego grafiku.");
    }
}