package antifraud.Repository;

import antifraud.Entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByIp(String ip);

    List<Transaction> findAllByNumber(String number);

    List<Transaction> findByDateBetween(Date prev, Date current);
}
