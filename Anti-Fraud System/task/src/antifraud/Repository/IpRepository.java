package antifraud.Repository;

import antifraud.Entity.IpAddress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpRepository extends CrudRepository<IpAddress, Long> {
    List<IpAddress> findAllByOrderByIdAsc();

    IpAddress findAllByIp(String ip);

    void deleteAllByIp(String ip);
}
