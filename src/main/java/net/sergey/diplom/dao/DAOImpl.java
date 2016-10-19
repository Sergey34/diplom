package net.sergey.diplom.dao;

import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.domain.ParserImplements;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DAOImpl implements DAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public DAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void saveSettings(Filter filter) {
        sessionFactory.getCurrentSession().saveOrUpdate(filter);
    }

    @Override
    public List<Filter> loadSetting(String name) {
        Criteria filter = sessionFactory.getCurrentSession().createCriteria(Filter.class).
                add(Restrictions.eq("username", name));
        @SuppressWarnings("unchecked")
        List<Filter> userNameList = filter.list();
        return userNameList;
    }

    @Override
    public List<ParserImplements> loadParsersImplementation() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ParserImplements.class);
        @SuppressWarnings("unchecked")
        List<ParserImplements> parserImplementsList = criteria.list();
        return parserImplementsList;
    }
}
