package projekt.AplikacjaFitnesSilownia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projekt.AplikacjaFitnesSilownia.Model.TreningPersonalny;

import java.util.List;

public interface TreningPersonalnyRepository extends JpaRepository<TreningPersonalny, Integer> {
    List<TreningPersonalny> findByStatus(String status);
    List<TreningPersonalny> findByTrenerId(Integer trenerId);
    List<TreningPersonalny> findByKlientId(Integer klientId);
}