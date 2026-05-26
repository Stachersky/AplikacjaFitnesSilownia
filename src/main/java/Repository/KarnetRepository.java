package Repository;

import Model.Karnet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KarnetRepository extends JpaRepository<Karnet, Integer> {
    Optional<Karnet> findByUzytkownikId(Integer uzytkownikId);
}