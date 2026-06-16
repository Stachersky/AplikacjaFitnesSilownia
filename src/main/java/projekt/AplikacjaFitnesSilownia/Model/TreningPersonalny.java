package projekt.AplikacjaFitnesSilownia.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "treningi_personalne")
@Data
public class TreningPersonalny {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "trener_id", nullable = false)
    private Uzytkownik trener;

    @ManyToOne
    @JoinColumn(name = "klient_id")
    private Uzytkownik klient; // Może być puste (null), dopóki ktoś się nie zapisze

    private LocalDateTime dataGodzina;

    private String status; // Np. "WOLNY" albo "ZAREZERWOWANY"
}