package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@WebAppConfiguration
public class ServiceImplTest {

    @Autowired
    DAO dao;
    @Autowired
    ServiceInt service;

    @Before
    public void before() {
        User user = new User();
        user.setUserName("11wwww2");
        user.setPassword("qweqwe");
        user.setUserRoles(new HashSet<UserRole>());
        user.setEnabled(1);
        dao.addUser(user);

        Menu menu = new Menu("header");
        List<MenuItem> menuItems = Arrays.asList(
                new MenuItem("qwe", "11115"),
                new MenuItem("qwe2", "11114"),
                new MenuItem("qwe3", "11113"),
                new MenuItem("qwe4", "11112")
        );
        menu.setMenuItems(menuItems);
        List<Menu> menus = Collections.singletonList(menu);
        dao.addMenus(menus);

        Airfoil airfoil = new Airfoil("qwe", "qqqq", new Prefix('A'), "eq");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
        airfoil = new Airfoil("qwe", "qqqq", new Prefix('B'), "eqB");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
        airfoil = new Airfoil("qwe", "qqqq", new Prefix('A'), "eq2");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
        airfoil = new Airfoil("qwe", "qqqq", new Prefix('A'), "eq23");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
        airfoil = new Airfoil("qwe", "qqqq", new Prefix('A'), "eq234");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
        airfoil = new Airfoil("qwe", "qqqq", new Prefix('A'), "eq123");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
    }

    @Test
    public void updateAirfoil() throws Exception {

    }

    @Test
    public void addUser() throws Exception {

    }

    @Test
    public void getAirfoilsByPrefix() throws Exception {


    }

    @Test
    public void updateGraf() throws Exception {

    }

    @Test
    public void getDetailInfo() throws Exception {

    }

    @Test
    public void addAirfoil() throws Exception {

    }


}