package net.sergey.diplom.dao.user;

import net.sergey.diplom.domain.user.Authorities;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DaoAuthorities extends CrudRepository<Authorities, String> {
    List<Authorities> findByUsername(String username);

    @Override
    List<Authorities> findAll();
}
