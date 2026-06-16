package projekt.AplikacjaFitnesSilownia.Repository;

import projekt.AplikacjaFitnesSilownia.Model.Rezerwacja;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RezerwacjaRepository extends JpaRepository<Rezerwacja, Integer> {
    // Dodajemy tę linijkę:
    List<Rezerwacja> findByUzytkownikId(Integer uzytkownikId);
}