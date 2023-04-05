package antifraud.Repository;

import antifraud.Entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByIp(String ip);

    Transaction findById(long id);

    List<Transaction> findAllByNumber(String number);

    List<Transaction> findAllByNumberAndDateBetween(String number, LocalDateTime prev, LocalDateTime current);

    List<Transaction> findAllByOrderByIdAsc();
}
