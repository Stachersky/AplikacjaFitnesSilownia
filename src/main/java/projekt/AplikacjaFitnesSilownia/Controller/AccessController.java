package projekt.AplikacjaFitnesSilownia.Controller;

import projekt.AplikacjaFitnesSilownia.Service.AccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) // Zabezpieczenie przed błędem CORS z Reacta
public class AccessController {

    private final AccessService accessService;

    public AccessController(AccessService accessService) {
        this.accessService = accessService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<String> checkIn(@RequestParam Integer uzytkownikId) {
        try {
            String wynik = accessService.rejestrujWejscie(uzytkownikId);
            return ResponseEntity.ok(wynik);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd wejścia: " + e.getMessage());
        }
    }
}