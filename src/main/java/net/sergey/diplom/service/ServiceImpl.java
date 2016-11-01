package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.service.utils.UtilRoles;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class ServiceImpl implements ServiceInt {

    //private final ApplicationContext applicationContext;
    private final DAO DAO;


    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    @Autowired
    public ServiceImpl(DAO dao) {
        DAO = dao;
    }

    /*@Autowired
    public ServiceImpl(ApplicationContext applicationContext, DAO DAO) {
        this.applicationContext = applicationContext;
        this.DAO = DAO;
    }*/

    @Override
    public List<Menu> getMenu() throws IOException {
        return DAO.getAllMenu();
    }

    @Override
    public List<User> getUser(String name) {
        return DAO.getUserByName(name);
    }

    @Override
    public boolean isValidUser(String name) {
        return getUser(name).size() == 0;
    }

    @Override
    public boolean addUser(User user) {
        try {
            DAO.addUser(user);
            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("пользователь с именем {} уже существует в базе. {}", user.getUserName(), e.getStackTrace());
            return false;
        }
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return DAO.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        UtilRoles.init(DAO.getAllUserRoles());
    }

    @Override
    public void clean() {
        DAO.cleanAllTables();
    }

    @Override
    public List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        return DAO.getAirfoilsByPrefix(prefix, startNumber, count);

    }
}
