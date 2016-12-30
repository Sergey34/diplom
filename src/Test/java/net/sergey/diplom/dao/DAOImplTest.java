package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@WebAppConfiguration
public class DAOImplTest {
    private static Configuration config;
    private static SessionFactory factory;
    private static Session hibernateSession;
    @Autowired
    private DAO dao;

    @BeforeClass
    public static void init() {
        /*config = new AnnotationConfiguration();
        File configFile = new File("src/Test/resources/testDao.xml");
        String path = configFile.getAbsolutePath();
        System.out.println(path);
        config.configure(configFile);
        factory = config.buildSessionFactory();
        hibernateSession = factory.openSession();*/
    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void addUser() throws Exception {
        User user = new User();
        user.setUserName("11wwww2");
        user.setPassword("qweqwe");
        user.setUserRoles(new HashSet<UserRole>());
        user.setEnabled(1);
        dao.addUser(user);
        List<User> userByName = dao.getUserByName("11wwww2");
        System.out.println(userByName);
    }

    @Test
    public void getUserByName() throws Exception {

    }

    @Test
    public void addMenus() throws Exception {

    }

    @Test
    public void getAllUserRoles() throws Exception {

    }

    @Test
    public void addAirfoils() throws Exception {

    }

    @Test
    public void addAirfoil() throws Exception {

    }

    @Test
    public void getAirfoilById() throws Exception {

    }

    @Test
    public void getCountAirfoilByPrefix1() throws Exception {

    }

    @Test
    public void getMenuItemByUrl() throws Exception {

    }

    @Test
    public void getCountAirfoilByPrefix() throws Exception {
        int a = dao.getCountAirfoilByPrefix('A');
    }

    @Test
    public void getAllMenu() throws Exception {
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
        long start = System.currentTimeMillis();
        List<Airfoil> a = dao.getAirfoilsByPrefix('G', 0, 0);//900-700
        long stop = System.currentTimeMillis();
        System.out.println("time: " + (stop - start));
    }

    @Test
    public void deleteProfileByPrefix() throws Exception {
        dao.getAirfoilsByPrefix('A', 0, 0);
    }
}