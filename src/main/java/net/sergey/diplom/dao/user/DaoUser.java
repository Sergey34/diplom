package net.sergey.diplom.dao.user;

import net.sergey.diplom.domain.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DaoUser extends CrudRepository<User, String> {
    @Override
    <S extends User> S save(S s);

    @Override
    <S extends User> Iterable<S> save(Iterable<S> iterable);

    User findOneByUserName(String username);

    boolean existByUserName(String username);
}
