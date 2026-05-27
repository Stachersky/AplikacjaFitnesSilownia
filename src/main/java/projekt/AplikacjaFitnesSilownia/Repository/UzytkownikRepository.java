package projekt.AplikacjaFitnesSilownia.Repository;

import projekt.AplikacjaFitnesSilownia.Model.Uzytkownik;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UzytkownikRepository extends CrudRepository<Uzytkownik, Integer> {

    // DODAJ TYLKO TĘ LINIJKĘ:
    Uzytkownik findByEmail(String email);
}