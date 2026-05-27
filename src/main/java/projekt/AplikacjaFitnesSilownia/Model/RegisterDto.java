package projekt.AplikacjaFitnesSilownia.Model;

import lombok.Data;

@Data
public class RegisterDto {
    private String imie;
    private String nazwisko;
    private String email;
    private String haslo;
}