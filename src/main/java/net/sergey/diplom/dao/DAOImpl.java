package net.sergey.diplom.dao;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.Profile;
import net.sergey.diplom.domain.User;
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
    public List<Profile> getProfilesByPrefix(char prefix) {
        return null;
    }

    @Override
    public List<Profile> getProfilesByName(String name) {
        return null;
    }

    @Override
    public List<Profile> getAllProfiles() {
        return null;
    }

    @Override
    public boolean addProfile(Profile profile) {
        return false;
    }

    @Override
    public Profile updateProfile(Profile profile) {
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
        for (Menu menu : menus) {
            save(menu);
        }
    }
}
