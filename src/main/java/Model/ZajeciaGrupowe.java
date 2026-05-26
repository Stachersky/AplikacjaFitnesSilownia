package Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "zajecia_grupowe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZajeciaGrupowe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nazwa;

    @ManyToOne
    @JoinColumn(name = "trener_id", nullable = false)
    private Uzytkownik trener;

    private LocalDateTime dataGodzina;
    private Integer limitMiejsc;
}