package projekt.AplikacjaFitnesSilownia.Controller;

import projekt.AplikacjaFitnesSilownia.Model.LoginDto;
import projekt.AplikacjaFitnesSilownia.Model.RegisterDto;
import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import projekt.AplikacjaFitnesSilownia.Repository.UzytkownikRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UzytkownikRepository uzytkownikRepo;

    public AuthController(UzytkownikRepository uzytkownikRepo) {
        this.uzytkownikRepo = uzytkownikRepo;
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

        return ResponseEntity.ok(uzytkownik);
    }
}