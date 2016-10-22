package net.sergey.diplom.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DAOImpl implements DAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public DAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


}
