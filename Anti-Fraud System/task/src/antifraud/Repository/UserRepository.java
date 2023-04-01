package antifraud.Repository;

import antifraud.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
        User findUserByUsername(String username);

        void deleteUserByUsername(String username);
}
