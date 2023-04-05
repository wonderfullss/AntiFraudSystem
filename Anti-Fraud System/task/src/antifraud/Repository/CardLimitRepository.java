package antifraud.Repository;

import antifraud.Entity.CardLimit;
import org.springframework.data.repository.CrudRepository;

public interface CardLimitRepository extends CrudRepository<CardLimit, Long> {
    CardLimit findByNumber(String number);
}
