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
import org.hibernate.criterion.Order;
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
    public List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        long start = System.currentTimeMillis();
        Prefix prefixTemplate = new Prefix(prefix);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Airfoil.class)
                .addOrder(Order.asc("shortName"))
                .createCriteria("prefix")
                .add(Example.create(prefixTemplate))

                .setFirstResult(startNumber);
        if (count != 0) {
            criteria.setMaxResults(count);
        }
        @SuppressWarnings("unchecked")
        List<Airfoil> airfoils = criteria.list();
        long stop = System.currentTimeMillis();
        System.out.println(stop - start);
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
    public void addAirfoil(List<Airfoil> airfoils) {
        Session currentSession = sessionFactory.getCurrentSession();
        for (Airfoil airfoil : airfoils) {
            currentSession.saveOrUpdate(airfoil);
        }
    }

    @Override
    public void addAirfoil(Airfoil airfoil) {
        save(airfoil);
    }

    @Override
    public int getIdLinkByUrl(String link) {
        return (int) sessionFactory.getCurrentSession().createSQLQuery("SELECT id FROM links WHERE link= :link").setParameter("link", link).uniqueResult();
    }

    @Override
    public Airfoil getAirfoilById(int id) {
        Airfoil airfoil = (Airfoil) sessionFactory.getCurrentSession().createCriteria(Airfoil.class).add(Restrictions.eq("id", id)).uniqueResult();
        if (null != airfoil) {
            Hibernate.initialize(airfoil.getCoordinates());
            Hibernate.initialize(airfoil.getAirfoilsSimilar());
        }
        return airfoil;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Airfoil> getAllAirfoil() {
        List<Airfoil> list = sessionFactory.getCurrentSession().createCriteria(Airfoil.class).list();
        for (Airfoil airfoil : list) {
            airfoil.setAirfoilsSimilar(null);
        }
        return list;
    }


}
