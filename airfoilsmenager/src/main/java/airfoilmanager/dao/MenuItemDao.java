package airfoilmanager.dao;

import base.domain.menu.MenuItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface MenuItemDao extends CrudRepository<MenuItem, Long> {

    void deleteByName(String name);
}
