package projekt.AplikacjaFitnesSilownia.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @PostMapping("/{id}/training-plan")
    public ResponseEntity<?> createTrainingPlan(@PathVariable Integer id, @RequestBody Map<String, Object> planDane) {
        System.out.println("✅ Java odebrała prośbę z Reacta o plan dla klienta: " + id);

        try {
            // Wzbogacamy paczkę o dane, których rygorystycznie domaga się C#
            planDane.put("UzytkownikId", id);
            planDane.put("NazwaPlanu", "Plan treningowy: " + planDane.get("cel"));
            planDane.put("Opis", "Spersonalizowany plan dla poziomu: " + planDane.get("poziom"));

            RestTemplate restTemplate = new RestTemplate();

            // Pamiętaj, żeby był tu Twój poprawny port z C# (np. 5158)
            String csharpUrl = "http://localhost:5158/api/Workouts/plans";

            System.out.println("⏳ Przekazuję pełne dane do C# pod adres: " + csharpUrl);

            ResponseEntity<String> odpowiedzCsharp = restTemplate.postForEntity(csharpUrl, planDane, String.class);

            return ResponseEntity.ok(odpowiedzCsharp.getBody());

        } catch (Exception e) {
            System.out.println("❌ Błąd połączenia z C#: " + e.getMessage());
            return ResponseEntity.status(500).body("Java odrzuciła lub nie połączyła się z C#: " + e.getMessage());
        }
    }
}