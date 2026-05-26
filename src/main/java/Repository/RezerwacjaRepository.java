package Repository;

import Model.Rezerwacja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RezerwacjaRepository extends JpaRepository<Rezerwacja, Integer> {
}