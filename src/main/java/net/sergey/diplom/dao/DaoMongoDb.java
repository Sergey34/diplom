package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public class DaoMongoDb implements DAO {
    private final MongoOperations mongoOperation;

    @Autowired
    public DaoMongoDb(MongoTemplate mongoTemplate) {
        this.mongoOperation = mongoTemplate;
    }

    public void foo() {
        Query searchUserQuery = new Query(Criteria.where("username").is("mkyong"));
        User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
        System.out.println(savedUser);
    }

    @Override
    public List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count, boolean isLazyLoad) {
        Query query =
                new Query().with(new Sort(Sort.Direction.ASC, "shortName"))
                        .addCriteria(Criteria.where("prefix").is(new Prefix(prefix))).limit(count);
        return mongoOperation.find(query, Airfoil.class);
    }

    @Override
    public List<Menu> getAllMenu() {
        return mongoOperation.findAll(Menu.class);
    }

    @Override
    public void addUser(User user) {
        mongoOperation.save(user);
    }

    @Override
    public User getUserByName(String name) {
        return mongoOperation.findOne(new Query(Criteria.where("username").is(name)), User.class);
    }

    @Override
    public void addMenus(List<Menu> menus) {
        for (Menu menu : menus) {
            mongoOperation.save(menu);
        }
    }

    @Override
    public List<Authorities> getAllUserRoles() {
        return mongoOperation.findAll(Authorities.class);
    }

    @Override
    public void addAirfoils(List<Airfoil> airfoils) {
        for (Airfoil airfoil : airfoils) {
            this.addAirfoil(airfoil);
        }
    }

    @Override
    public void addAirfoil(Airfoil airfoil) {
        mongoOperation.save(airfoil);
    }

    @Override
    public Airfoil getAirfoilById(String id) {
        return mongoOperation.findOne(new Query(Criteria.where("shortName").is(id)), Airfoil.class);
    }

    @Override
    public int getCountAirfoilByPrefix(char prefix) {
        return (int) mongoOperation.count(new Query(Criteria.where("prefix").is(new Prefix(prefix))), Airfoil.class);
    }

    @Override
    public MenuItem getMenuItemByUrl(String prefix) {

        return mongoOperation.findOne(new Query(Criteria.where("urlCode").is(prefix)), MenuItem.class);
    }

    @Override
    public List<Airfoil> getAllAirfoils(int startNumber, int count, boolean isLasyLoad) {
        return null;
    }

    @Override
    public List<Authorities> getRoleByUsername(String name) {
        return mongoOperation.find(new Query(Criteria.where("username").is(name)), Authorities.class);
    }

    @Override
    public void addAuthorities(List<Authorities> authorities) {
        for (Authorities authority : authorities) {
            addAuthority(authority);
        }
    }

    @Override
    public void addAuthority(Authorities authority) {
        mongoOperation.save(authority);
    }

    @Override
    public void delete(String id) {
        mongoOperation.remove(getAirfoilById(id));
    }
}
