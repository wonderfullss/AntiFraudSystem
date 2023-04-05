package antifraud.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("transactionId")
    private Long id;

    private long amount;

    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)(?:\\.(?!$)|$)){4}$")
    private String ip;

    @Pattern(regexp = "^4[0-9]{12}(?:[0-9]{3})?$")
    private String number;


    private String region;

    private LocalDateTime date;

    private String result;

    @JsonIgnore
    private String feedback;

    @JsonProperty("feedback")
    public String getFeedback() {
        return feedback == null ? "" : feedback;
    }
}
