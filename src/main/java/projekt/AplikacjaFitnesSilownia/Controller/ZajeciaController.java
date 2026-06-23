package projekt.AplikacjaFitnesSilownia.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projekt.AplikacjaFitnesSilownia.Model.Rezerwacja;
import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import projekt.AplikacjaFitnesSilownia.Model.ZajeciaGrupowe;
import projekt.AplikacjaFitnesSilownia.Repository.RezerwacjaRepository; // <--- TEGO IMPORTU BRAKOWAŁO
import projekt.AplikacjaFitnesSilownia.Repository.UzytkownikRepository;
import projekt.AplikacjaFitnesSilownia.Repository.ZajeciaGrupoweRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/zajecia")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ZajeciaController {

    private final ZajeciaGrupoweRepository zajeciaRepo;
    private final RezerwacjaRepository rezerwacjaRepo;
    private final UzytkownikRepository uzytkownikRepo;

    public ZajeciaController(ZajeciaGrupoweRepository zajeciaRepo, RezerwacjaRepository rezerwacjaRepo, UzytkownikRepository uzytkownikRepo) {
        this.zajeciaRepo = zajeciaRepo;
        this.rezerwacjaRepo = rezerwacjaRepo;
        this.uzytkownikRepo = uzytkownikRepo;
    }

    // 1. Odbiór całego grafiku dla klienta
    @GetMapping("/grafik")
    public ResponseEntity<List<ZajeciaGrupowe>> pobierzGrafik() {
        return ResponseEntity.ok(zajeciaRepo.findAll());
    }

    // 2. Rezerwacja miejsca przez klienta
    @PostMapping("/{zajeciaId}/rezerwuj")
    public ResponseEntity<String> rezerwujZajecia(@PathVariable Integer zajeciaId, @RequestParam Integer uzytkownikId) {

        Optional<ZajeciaGrupowe> zajeciaOpt = zajeciaRepo.findById(zajeciaId);
        if (zajeciaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Błąd: Nie znaleziono takich zajęć w grafiku.");
        }

        Uzytkownik klient = uzytkownikRepo.findById(uzytkownikId).orElse(null);
        if (klient == null) {
            return ResponseEntity.badRequest().body("❌ Błąd autoryzacji: Nie znaleziono klienta.");
        }

        ZajeciaGrupowe zajecia = zajeciaOpt.get();

        // Zapis do bazy MySQL
        Rezerwacja nowaRezerwacja = new Rezerwacja();
        nowaRezerwacja.setZajeciaGrupowe(zajecia);
        nowaRezerwacja.setUzytkownik(klient);
        nowaRezerwacja.setStatus("POTWIERDZONA");

        rezerwacjaRepo.save(nowaRezerwacja);

        return ResponseEntity.ok("✅ Pomyślnie zarezerwowano miejsce na zajęcia: " + zajecia.getNazwa() + "!");
    }

    @PostMapping("/dodaj")
    public ResponseEntity<String> dodajZajecia(@RequestParam Integer trenerId, @RequestBody java.util.Map<String, Object> dane) {

        // Sprawdzamy, czy ten kto dodaje, na pewno jest instruktorem
        Uzytkownik trener = uzytkownikRepo.findById(trenerId).orElse(null);
        if (trener == null || !"ROLE_INSTRUCTOR".equals(trener.getRola())) {
            return ResponseEntity.status(403).body("❌ Odmowa dostępu: Tylko instruktor może dodawać zajęcia!");
        }

        try {
            ZajeciaGrupowe noweZajecia = new ZajeciaGrupowe();
            noweZajecia.setNazwa((String) dane.get("nazwa"));
            noweZajecia.setLimitMiejsc(Integer.parseInt(dane.get("limitMiejsc").toString()));

            // Konwertujemy datę z formularza HTML ("YYYY-MM-DDThh:mm") na format Javy
            noweZajecia.setDataGodzina(java.time.LocalDateTime.parse((String) dane.get("dataGodzina")));
            noweZajecia.setTrener(trener);

            zajeciaRepo.save(noweZajecia);
            return ResponseEntity.ok("✅ Sukces! Zajęcia '" + noweZajecia.getNazwa() + "' zostały dodane do grafiku.");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Błąd formularza: Sprawdź poprawność wprowadzonych danych. " + e.getMessage());
        }
    }

    // 4. Klient pobiera listę swoich zapisów na zajęcia
    @GetMapping("/klient/{klientId}")
    public ResponseEntity<List<Rezerwacja>> mojeZajecia(@PathVariable Integer klientId) {
        return ResponseEntity.ok(rezerwacjaRepo.findByUzytkownikId(klientId));
    }

    // 5. Klient anuluje swoją rezerwację na zajęcia
    @DeleteMapping("/rezerwacja/{rezerwacjaId}/anuluj")
    public ResponseEntity<String> anulujRezerwacjeZajec(@PathVariable Integer rezerwacjaId) {
        rezerwacjaRepo.deleteById(rezerwacjaId);
        return ResponseEntity.ok("✅ Pomyślnie wypisano z zajęć.");
    }

    // 6. Instruktor całkowicie usuwa zajęcia z grafiku
    @DeleteMapping("/{zajeciaId}/usun")
    public ResponseEntity<String> usunZajecia(@PathVariable Integer zajeciaId) {
        try {
            zajeciaRepo.deleteById(zajeciaId);
            return ResponseEntity.ok("✅ Usunięto zajęcia z grafiku.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Nie można usunąć zajęć (prawdopodobnie są już na nie zapisani klienci).");
        }
    }
}