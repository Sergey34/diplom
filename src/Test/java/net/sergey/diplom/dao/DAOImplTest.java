package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@WebAppConfiguration
public class DAOImplTest {
    @Autowired
    private DAO dao;

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