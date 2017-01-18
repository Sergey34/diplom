package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.UserView;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

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
    public void addUser() throws Exception {
        UserView user = new UserView();
        user.setName("11wwww2");
        user.setPassword("qweqwe");
        user.setRole(new ArrayList<String>());
        Message message = service.addUser(user);
        Assert.assertTrue(message.getMessage().equals("Пользователь с таким именем уже существует, Выберите другое имя"));
        user.setName("231231");
        message = service.addUser(user);
        Assert.assertTrue(message.getMessage().equals("Пользователь успешно создан"));
    }

    @Test
    public void getAirfoilsByPrefix() throws Exception {
        List<AirfoilDTO> airfoilsByPrefix = service.getAirfoilsDtoByPrefix('A', 0, -1);
        Assert.assertNotNull(airfoilsByPrefix);
        Assert.assertTrue(airfoilsByPrefix.size() == 5);

        airfoilsByPrefix = service.getAirfoilsDtoByPrefix('A', 0, 100);
        Assert.assertNotNull(airfoilsByPrefix);
        Assert.assertTrue(airfoilsByPrefix.size() == 5);

        airfoilsByPrefix = service.getAirfoilsDtoByPrefix('A', 10, 100);
        Assert.assertNotNull(airfoilsByPrefix);
        Assert.assertTrue(airfoilsByPrefix.size() == 0);

    }


    @Test
    public void getDetailInfo() throws Exception {
        service.getDetailInfo("eqb");
        service.getDetailInfo(null);
        service.getDetailInfo("");
    }


}