package net.sergey.diplom.dao.menu;

import net.sergey.diplom.domain.menu.Menu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DaoMenu extends CrudRepository<Menu, Integer> {
    @Override
    List<Menu> findAll();

    @Override
    <S extends Menu> S save(S s);

    @Override
    <S extends Menu> Iterable<S> save(Iterable<S> iterable);
}
