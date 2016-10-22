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

import java.util.List;
import java.util.Set;

@Repository
public class DAOImpl implements DAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public DAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Set<Profile> getProfilesByPrefix(char prefix) {
        return null;
    }

    @Override
    public Set<Profile> getProfilesByName(String name) {
        return null;
    }

    @Override
    public Set<Profile> getAllProfiles() {
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
    public Set<Menu> getAllMenu() {
        return null;
    }

    @Override
    public Set<Menu> getMenuByHeader(String header) {
        return null;
    }

    @Override
    public boolean addMenu(Menu menu) {
        return false;
    }

    @Override
    public Menu updateMenu(Menu menu) {
        return null;
    }

    @Override
    public boolean addUser(User user) {
        return false;
    }

    @Override
    public void updateUserPasword(String password) {

    }

    @Override
    public List<User> getUserById(String name) {
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
}
