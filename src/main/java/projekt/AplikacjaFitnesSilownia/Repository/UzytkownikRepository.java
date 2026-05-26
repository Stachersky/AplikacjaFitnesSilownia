package projekt.AplikacjaFitnesSilownia.Repository;

import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Integer> {
    Optional<Uzytkownik> findByEmail(String email);
}