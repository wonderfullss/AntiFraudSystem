package antifraud.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "card_limit")
public class CardLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private long allowed;

    private long manualProcessing;

    private long prohibited;

    public CardLimit(String number, long allowed, long manualProcessing, long prohibited) {
        this.number = number;
        this.allowed = allowed;
        this.manualProcessing = manualProcessing;
        this.prohibited = prohibited;
    }
}
