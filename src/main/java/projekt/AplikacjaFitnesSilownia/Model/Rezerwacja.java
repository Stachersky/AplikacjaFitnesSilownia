package projekt.AplikacjaFitnesSilownia.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rezerwacje")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rezerwacja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "zajecia_id", nullable = false)
    private ZajeciaGrupowe zajeciaGrupowe;

    @ManyToOne
    @JoinColumn(name = "uzytkownik_id", nullable = false)
    private Uzytkownik uzytkownik;

    private String status;
}