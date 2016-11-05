package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Links;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilView;
import net.sergey.diplom.service.parser.UtilParser;
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
import java.util.Set;

@Service
public class ServiceImpl implements ServiceInt {

    //private final ApplicationContext applicationContext;
    private final DAO dao;


    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    @Autowired
    public ServiceImpl(DAO dao) {
        this.dao = dao;
    }

    /*@Autowired
    public ServiceImpl(ApplicationContext applicationContext, dao dao) {
        this.applicationContext = applicationContext;
        this.dao = dao;
    }*/

    @Override
    public List<Menu> getMenu() throws IOException {
        return dao.getAllMenu();
    }

    @Override
    public List<User> getUser(String name) {
        return dao.getUserByName(name);
    }

    @Override
    public boolean isValidUser(String name) {
        return getUser(name).size() == 0;
    }

    @Override
    public boolean addUser(User user) {
        try {
            dao.addUser(user);
            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("пользователь с именем {} уже существует в базе. {}", user.getUserName(), e.getStackTrace());
            return false;
        }
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return dao.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        UtilRoles.init(dao.getAllUserRoles());
    }

    @Override
    public void clean() {
        dao.cleanAllTables();
    }

    @Override
    public List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        return dao.getAirfoilsByPrefix(prefix, startNumber, count);

    }

    @Override
    public boolean updateAirfoil(AirfoilView airfoilView) {
        Airfoil airfoil = new Airfoil(airfoilView.getName(),
                airfoilView.getDescription(),
                airfoilView.getImage(),
                airfoilView.getId(),
                new Prefix(airfoilView.getPrefix().charAt(0)));
        Set<Links> links = UtilParser.parseLinks(airfoilView.getLink());
        links = setIdLinkByUrl(links);
        airfoil.setLinks(links);
        System.out.println(airfoil);
        dao.addAirfoils(airfoil);
        return false;
    }

    private Set<Links> setIdLinkByUrl(Set<Links> links) {
        for (Links link : links) {
            link.setId(dao.getIdLinkByUrl(link.getLink()));
        }
        return links;
    }
}
