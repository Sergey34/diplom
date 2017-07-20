package net.sergey.diplom.dao.menu;

import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface DaoMenu extends CrudRepository<Menu, ObjectId>, PagingAndSortingRepository<Menu, ObjectId> {
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    List<Menu> findAll();

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Menu findMenuByItemsContains(MenuItem url);

    @Transactional(propagation = Propagation.REQUIRED)
    Collection<Menu> save(Collection<Menu> iterable);
}
