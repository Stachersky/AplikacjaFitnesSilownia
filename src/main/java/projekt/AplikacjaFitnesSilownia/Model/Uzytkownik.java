package projekt.AplikacjaFitnesSilownia.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "uzytkownicy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Uzytkownik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imie;
    private String nazwisko;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String haslo;

    private String rola;
}