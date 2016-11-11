package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Airfoil> getAirfoilsWithLinksByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoils = getAirfoilsByPrefix(prefix, startNumber, count);
        for (Airfoil airfoil : airfoils) {
            Hibernate.initialize(airfoil.getLinks());
        }
        return airfoils;
    }

    @Override
    public List<Airfoil> getAirfoilsWithCoordinatesByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoils = getAirfoilsByPrefix(prefix, startNumber, count);
        for (Airfoil airfoil : airfoils) {
            Hibernate.initialize(airfoil.getCoordinates());
        }
        return airfoils;
    }

    private List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        Prefix prefixTemplate = new Prefix(prefix);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Airfoil.class)
                .createCriteria("prefix")
                .add(Example.create(prefixTemplate)).setFirstResult(startNumber);
        if (count != 0) {
            criteria.setMaxResults(count);
        }
        @SuppressWarnings("unchecked")
        List<Airfoil> airfoils = criteria.list();
        return airfoils;
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
    public void addUser(User user) {
        save(user);
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
    public void addMenus(List<Menu> menus) {
        for (Menu menu : menus) {
            save(menu);
        }
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
    public void addAirfoils(List<Airfoil> airfoils) {
        Session currentSession = sessionFactory.getCurrentSession();
        for (Airfoil airfoil : airfoils) {
            currentSession.merge(airfoil);
        }
    }

    @Override
    public void addAirfoils(Airfoil airfoil) {
        save(airfoil);
    }

    @Override
    public int getIdLinkByUrl(String link) {
        //// TODO: 06.11.16 переделать на параметр
        return (int) sessionFactory.getCurrentSession().createSQLQuery("SELECT id FROM links WHERE link='" + link + '\'').list().get(0);
    }
}
