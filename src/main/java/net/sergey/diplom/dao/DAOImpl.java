package net.sergey.diplom.dao;

import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.domain.ParserImplements;
import net.sergey.diplom.domain.SettingLoadJSON;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DAOImpl implements DAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void saveSettings(Filter filter) {
        sessionFactory.getCurrentSession().saveOrUpdate(filter);
    }

    @SuppressWarnings("unchecked")
    public List<SettingLoadJSON> loadSetting(String SQL) {
        return sessionFactory.getCurrentSession().createSQLQuery(SQL).addEntity(SettingLoadJSON.class).list();
    }

    @Override
    public List<ParserImplements> loadParsersImplementation(String s) {
        return sessionFactory.getCurrentSession().createSQLQuery(s).addEntity(ParserImplements.class).list();
    }
}
