package projekt.AplikacjaFitnesSilownia.Service;

import projekt.AplikacjaFitnesSilownia.Model.TrainingPlanDto;
import projekt.AplikacjaFitnesSilownia.Model.WorkoutResultDto;
import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import projekt.AplikacjaFitnesSilownia.Repository.UzytkownikRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WorkoutService {

    private final RestClient netApiClient;
    private final UzytkownikRepository uzytkownikRepo;

    // Musieliśmy dodać uzytkownikRepo do konstruktora, żeby móc pytać bazę MySQL o role
    public WorkoutService(RestClient netApiClient, UzytkownikRepository uzytkownikRepo) {
        this.netApiClient = netApiClient;
        this.uzytkownikRepo = uzytkownikRepo;
    }

    public String zakonczTrening(Integer sessionId, WorkoutResultDto dto) {
        try {
            ResponseEntity<String> response = netApiClient.post()
                    .uri("/Workouts/sessions/" + sessionId + "/analyze")
                    .body(dto)
                    .retrieve()
                    .toEntity(String.class);
            return "Odpowiedź z modułu analitycznego .NET: " + response.getBody();
        } catch (Exception e) {
            return "Błąd integracji z systemem fitness: " + e.getMessage();
        }
    }

    // NOWA METODA: Tworzenie planu z weryfikacją roli w MySQL
    public String stworzPlan(Integer instruktorId, TrainingPlanDto dto) {
        // Szukamy, kto próbuje dodać plan
        Uzytkownik instruktor = uzytkownikRepo.findById(instruktorId).orElse(null);

        // Jeśli tej osoby nie ma, albo nie ma roli ROLE_INSTRUCTOR - blokujemy!
        if (instruktor == null || !instruktor.getRola().equals("ROLE_INSTRUCTOR")) {
            return "❌ Odmowa dostępu: Tylko certyfikowany instruktor może tworzyć plany treningowe!";
        }

        try {
            // Jeśli wszystko jest OK, wysyłamy plan do C#
            ResponseEntity<String> response = netApiClient.post()
                    .uri("/Workouts/plans")
                    .body(dto)
                    .retrieve()
                    .toEntity(String.class);
            return "✅ Sukces: " + response.getBody();
        } catch (Exception e) {
            return "Błąd zapisu planu: " + e.getMessage();
        }
    }
}