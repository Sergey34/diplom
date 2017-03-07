package net.sergey.diplom.dao.menu;

import net.sergey.diplom.domain.menu.MenuItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DaoMenuItem extends CrudRepository<MenuItem, Integer> {

    MenuItem findOneByUrl(String url);
}
