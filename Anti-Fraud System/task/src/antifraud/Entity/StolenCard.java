package antifraud.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "stolen_card")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StolenCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^4[0-9]{12}(?:[0-9]{3})?$")
    private String number;
}
