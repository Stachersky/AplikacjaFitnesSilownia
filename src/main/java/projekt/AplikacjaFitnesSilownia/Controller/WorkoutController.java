package projekt.AplikacjaFitnesSilownia.Controller;

import projekt.AplikacjaFitnesSilownia.Model.WorkoutResultDto;
import projekt.AplikacjaFitnesSilownia.Service.WorkoutService;
import projekt.AplikacjaFitnesSilownia.Model.TrainingPlanDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")

public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/sessions/{id}/complete")
    public ResponseEntity<String> completeSession(@PathVariable Integer id, @RequestBody WorkoutResultDto dto) {
        String wynik = workoutService.zakonczTrening(id, dto);
        return ResponseEntity.ok(wynik);
    }

    @PostMapping("/plans")
    public ResponseEntity<String> createPlan(@RequestParam Integer instruktorId, @RequestBody TrainingPlanDto dto) {
        String wynik = workoutService.stworzPlan(instruktorId, dto);
        return ResponseEntity.ok(wynik);
    }
}