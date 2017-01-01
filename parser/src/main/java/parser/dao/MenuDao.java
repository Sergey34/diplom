package parser.dao;

import base.domain.menu.Menu;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface MenuDao extends CrudRepository<Menu, Long> {

    @Override
    <S extends Menu> S save(S s);

    @Override
    <S extends Menu> Iterable<S> save(Iterable<S> iterable);

    @Override
    Menu findOne(Long aLong);

    @Override
    Iterable<Menu> findAll();
}
