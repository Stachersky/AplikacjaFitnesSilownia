package projekt.AplikacjaFitnesSilownia.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/karnety")
public class KarnetController {

    @PostMapping("/kup/{userId}")
    public ResponseEntity<String> kupKarnet(@PathVariable Integer userId, @RequestBody String nazwaKarnetu) {

        System.out.println("✅ Otrzymano płatność! Klient ID: " + userId + " kupuje: " + nazwaKarnetu);

        // TODO: Tutaj w przyszłości dodamy zapis do bazy danych MySQL (KarnetRepository)
        // np. karnetRepository.save(nowyKarnet);

        return ResponseEntity.ok("Twój " + nazwaKarnetu + " został pomyślnie aktywowany! Dziękujemy.");
    }
}