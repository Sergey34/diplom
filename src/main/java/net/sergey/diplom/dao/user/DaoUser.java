package net.sergey.diplom.dao.user;

import net.sergey.diplom.domain.user.User;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface DaoUser extends CrudRepository<User, ObjectId>, PagingAndSortingRepository<User, ObjectId> {

    @Override
    <S extends User> S save(S s);

    @RestResource(path = "/username")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    User findOneByUserName(String username);

}
