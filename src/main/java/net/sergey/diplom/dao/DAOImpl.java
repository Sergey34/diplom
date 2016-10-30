package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository
public class DAOImpl implements DAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public DAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Airfoil> getProfilesByPrefix(char prefix) {
        return null;
    }

    @Override
    public List<Airfoil> getProfilesByName(String name) {
        return null;
    }

    @Override
    public List<Airfoil> getAllProfiles() {
        return null;
    }

    @Override
    public boolean addProfile(Airfoil profile) {
        return false;
    }

    @Override
    public Airfoil updateProfile(Airfoil profile) {
        return null;
    }

    @Override
    public boolean deleteProfileById(int id) {
        return false;
    }

    @Override
    public boolean deleteProfileByName(String name) {
        return false;
    }

    @Override
    public boolean deleteProfileByPrefix(char prefix) {
        return false;
    }

    @Override
    public List<Menu> getAllMenu() {
        @SuppressWarnings("unchecked")
        List<Menu> menus = sessionFactory.getCurrentSession().createCriteria(Menu.class).list();
        for (Menu menu : menus) {
            if (menu != null) {
                Hibernate.initialize(menu.getMenuItems());
            }
        }
        return menus;
    }

    @Override
    public List<Menu> getMenuByHeader(String header) {
        return null;
    }

    @Override
    public void addMenu(Menu menu) {
        save(menu);
    }

    @Override
    public Menu updateMenu(Menu menu) {
        return null;
    }

    @Override
    public void addUser(User user) {
        save(user);
    }

    @Override
    public void updateUserPassword(String password) {

    }

    @Override
    public List<User> getUserByName(String name) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class)
                .add(Restrictions.eq("userName", name));
        @SuppressWarnings("unchecked")
        List<User> users = criteria.list();
        for (User user : users) {
            if (user != null) {
                Hibernate.initialize(user.getUserRoles());
            }
        }
        return users;
    }


    private <T> void save(T object) {
        sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    @Override
    public void addMenus(ArrayList<Menu> menus) {
        menus.forEach(this::save);
        /*for (Menu menu : menus) {
            save(menu);
        }*/
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserRole> getAllUserRoles() {
        return sessionFactory.getCurrentSession().createCriteria(UserRole.class).list();
    }

    @Override
    public void cleanAllTables() {
        //// TODO: 30.10.16 список имен таблиц кроме системных (пользователи, настройки парсеров)
        sessionFactory.getCurrentSession().createSQLQuery("DELETE FROM links");
    }

    @Override
    public void addPrefix(Prefix prefix1) {
        save(prefix1);
    }
}
