package antifraud.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "ip")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4]\\d|1?\\d?\\d)(?:\\.(?!$)|$)){4}$")
    private String ip;
}
