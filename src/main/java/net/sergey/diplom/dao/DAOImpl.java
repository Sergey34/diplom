package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Prefix;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
@Primary
public class DAOImpl implements DAO {
    private final SessionFactory sessionFactory;

    @Autowired
    public DAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count, boolean isLazyLoad) {
        Prefix prefixTemplate = new Prefix(prefix);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Airfoil.class)
                .addOrder(Order.asc("shortName"))
                .createCriteria("prefix")
                .add(Example.create(prefixTemplate))
                .setFirstResult(startNumber);
        if (count > 0) {
            criteria.setMaxResults(count);
        }
        @SuppressWarnings("unchecked")
        List<Airfoil> airfoils = criteria.list();
        if (isLazyLoad) {
            return airfoils;
        }
        for (Airfoil airfoil : airfoils) {
            Hibernate.initialize(airfoil.getPrefix());
            Hibernate.initialize(airfoil.getCoordinates());
        }
        return airfoils;
    }

    private <T> void save(T object) {
        sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    @Override
    public void addAirfoils(List<Airfoil> airfoils) {
        Session currentSession = sessionFactory.getCurrentSession();
        for (Airfoil airfoil : airfoils) {
            currentSession.merge(airfoil);
        }
    }

    @Override
    public void addAirfoil(Airfoil airfoil) {
        save(airfoil);
    }


    @Override
    public Airfoil getAirfoilById(String id) {
        Airfoil airfoil = (Airfoil) sessionFactory.getCurrentSession()
                .createCriteria(Airfoil.class).add(Restrictions.eq("shortName", id)).uniqueResult();
        if (null != airfoil) {
            Hibernate.initialize(airfoil.getCoordinates());
            Hibernate.initialize(airfoil.getPrefix());
        }
        return airfoil;
    }

    @Override
    public int getCountAirfoilByPrefix(char prefix) {
        Prefix prefixTemplate = new Prefix(prefix);
        return sessionFactory.getCurrentSession().createCriteria(Airfoil.class).createCriteria("prefix")
                .add(Example.create(prefixTemplate)).list().size();
    }


    @Override
    public List<Airfoil> getAllAirfoils(int startNumber, int count, boolean isLazyLoad) {
        @SuppressWarnings("unchecked")
        List<Airfoil> airfoils = sessionFactory.getCurrentSession().createCriteria(Airfoil.class).setFirstResult(startNumber).setMaxResults(count).list();
        if (isLazyLoad) {
            return airfoils;
        }
        for (Airfoil airfoil : airfoils) {
            Hibernate.initialize(airfoil.getCoordinates());
            Hibernate.initialize(airfoil.getPrefix());
        }
        return airfoils;
    }

    @Override
    public void delete(String id) {
        sessionFactory.getCurrentSession().delete(getAirfoilById(id));
    }


}
