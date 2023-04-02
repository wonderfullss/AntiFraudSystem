package antifraud.Repository;

import antifraud.Entity.IpAddress;
import antifraud.Entity.StolenCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    List<StolenCard> findAllByOrderByIdAsc();

    StolenCard findAllByNumber(String number);

    void deleteAllByNumber(String number);
}
