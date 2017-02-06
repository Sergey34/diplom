package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@WebAppConfiguration
public class DAOImplTest {
    @Autowired
    private DAO dao;


    @Test
    public void addUser() throws Exception {
        User user = new User();
        user.setUserName("11wwww2");
        user.setPassword("qweqwe");
        user.setAuthorities(new ArrayList<Authorities>());
        user.setEnabled(true);
        dao.addUser(user);
        User userByName = dao.getUserByName("11wwww2");
        Assert.assertTrue(user.getUserName().equals(userByName.getUserName()));
    }

    @Test
    public void getUserByName() throws Exception {
        User userByName = dao.getUserByName("");
        Assert.assertNull(userByName);
    }

    @Test
    public void addMenus() throws Exception {
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
        List<Menu> allMenu = dao.getAllMenu();
        System.out.println(menu.equals(allMenu.get(0)));
        for (int i = 0; i < allMenu.size(); i++) {
            Assert.assertTrue(allMenu.get(i).equals(menus.get(i)));
        }
    }

    @Test
    public void getAllUserRoles() throws Exception {
        List<Authorities> allUserRoles = dao.getAllUserRoles();
        Assert.assertNotNull(allUserRoles);
    }

    @Test
    public void addAirfoils() throws Exception {
        Airfoil airfoil = new Airfoil("qwe", "qqqq", new Prefix('q'), "eq");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoils(Arrays.asList(airfoil));
    }

    @Test
    public void addAirfoil() throws Exception {
        Airfoil airfoil = new Airfoil("qwe", "qqqq", new Prefix('q'), "eq");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
    }

    @Test
    public void updateAirfoil() throws Exception {
        Airfoil airfoil = new Airfoil("qwe", "qqqq", new Prefix('q'), "eq");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
        dao.addAirfoil(airfoil);
    }

    @Test
    public void getAirfoilById() throws Exception {
        Airfoil airfoil = new Airfoil("qwe", "qqqq", new Prefix('q'), "eq");
        airfoil.setCoordinates(Collections.<Coordinates>emptySet());
        dao.addAirfoil(airfoil);
        Airfoil eq = dao.getAirfoilById("eq");
        Assert.assertTrue(eq.equals(airfoil));
    }


    @Test
    public void getMenuItemByUrl() throws Exception {
        Menu menu = new Menu("header");
        List<MenuItem> menuItems = Arrays.asList(
                new MenuItem("qwe", "1"),
                new MenuItem("qwe2", "12"),
                new MenuItem("qwe3", "11113"),
                new MenuItem("qwe4", "11112")
        );
        menu.setMenuItems(menuItems);
        List<Menu> menus = Collections.singletonList(menu);
        dao.addMenus(menus);
        dao.getMenuItemByUrl("11113");
    }

    @Test
    public void getCountAirfoilByPrefix() throws Exception {
        Airfoil airfoil = new Airfoil("qwe", "qqqq", new Prefix('A'), "eq");
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
        int count = dao.getCountAirfoilByPrefix('A');
        Assert.assertTrue(count == 5);
    }

    @Test
    public void getAllMenu() throws Exception {
        Menu menuTmp = new Menu("header");
        List<MenuItem> menuItems = Arrays.asList(
                new MenuItem("qwe", "1"),
                new MenuItem("qwe2", "12"),
                new MenuItem("qwe3", "11113"),
                new MenuItem("qwe4", "11112")
        );
        menuTmp.setMenuItems(menuItems);
        List<Menu> menus = Collections.singletonList(menuTmp);
        dao.addMenus(menus);
        List<Menu> allMenu = dao.getAllMenu();
        assertTrue(allMenu != null);
        for (Menu menu : allMenu) {
            assertTrue(menu != null);
            assertTrue(menu.getMenuItems() != null);
            for (MenuItem menuItem : menu.getMenuItems()) {
                assertTrue(menuItem != null);
            }
        }
    }

    @Test
    public void getAirfoilsByPrefix() throws Exception {
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
        long start = System.currentTimeMillis();
        List<Airfoil> airfoils = dao.getAirfoilsByPrefix('A', 0, 0, true);//900-700
        Assert.assertNotNull(airfoils);
        Assert.assertTrue(airfoils.size() == 5);
        for (Airfoil airfoil1 : airfoils) {
            Assert.assertFalse(airfoil1.getPrefix().getPrefix() != 'A');
        }
        long stop = System.currentTimeMillis();
        System.out.println("time: " + (stop - start));
    }
}