package net.sergey.diplom.dao.menu;

import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DaoMenu extends CrudRepository<Menu, ObjectId>, PagingAndSortingRepository<Menu, ObjectId> {
    @Override
    List<Menu> findAll();

    Menu findMenuByItemsContains(MenuItem url);
}
