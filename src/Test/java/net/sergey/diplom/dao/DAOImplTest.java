package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Links;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class DAOImplTest {
    @Autowired
    private DAO dao;

    @Test
    public void getAirfoilsByPrefix() throws Exception {
        long start = System.currentTimeMillis();
        List<Airfoil> a = dao.getAirfoilsWithLinksByPrefix('G', 0, 0);//900-700
        System.out.println(a.get(0).getLinks());
        long stop = System.currentTimeMillis();
        System.out.println("time: " + (stop - start));
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
    public void cleanAllTables() throws Exception {

    }

    @Test
    public void addAirfoils() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void addAirfoils1() throws Exception {

    }


    @Test
    public void getIdLinkByUrl() throws Exception {
        int idLinkByUrl = dao.getIdLinkByUrl("/airfoil/details?airfoil=a18-il");
        assertTrue(idLinkByUrl == 563);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    @Test
    public void getProfilesByPrefix() throws Exception {
    }

    @Test
    public void getProfilesByName() throws Exception {
        Airfoil airfoil = new Airfoil("name", "discription", "img", new Prefix('Z'));
        Set<Links> linkses = new HashSet<>();
        linkses.add(new Links("url", "name"));
        linkses.add(new Links("url2", "name2"));
        airfoil.setLinks(linkses);
        System.out.println(airfoil);
        dao.addAirfoils(airfoil);
    }

    @Test
    public void getAllProfiles() throws Exception {

    }

    @Test
    public void addProfile() throws Exception {

    }

    @Test
    public void updateProfile() throws Exception {

    }

    @Test
    public void deleteProfileById() throws Exception {

    }

    @Test
    public void deleteProfileByName() throws Exception {

    }

    @Test
    public void deleteProfileByPrefix() throws Exception {
        dao.getAirfoilsWithLinksByPrefix('A', 0, 0);

    }

    @Test
    public void getAllMenu() throws Exception {

    }

    @Test
    public void getMenuByHeader() throws Exception {

    }

    @Test
    public void addMenu() throws Exception {

    }

    @Test
    public void updateMenu() throws Exception {

    }

    @Test
    public void addUser() throws Exception {
        User user = new User();
        user.setId(3);
        user.setUserName("Sergey");
        user.setPassword("343434");
        user.setEnabled(1);

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setRoleId(1);
        userRole.setRole("ROLE_USER");

        UserRole userRole2 = new UserRole();
        userRole2.setRoleId(3);
        userRole2.setRole("ROLE_SUPER_ADMIN");

        userRoles.add(userRole2);
        userRoles.add(userRole);

        user.setUserRoles(userRoles);
        System.out.println(user);
        try {
            dao.addUser(user);
        } catch (ConstraintViolationException e) {
            throw new DuplicateKeyException("duplicated key");
        }

    }

    @Test
    public void updateUserPassword() throws Exception {
        List<User> alex = dao.getUserByName("alex");
        User user = alex.get(0);
        user.setPassword("123456");
        dao.addUser(user);
    }


    @Test
    public void getUserById() throws Exception {
        List<User> users = dao.getUserByName("alex");
        System.out.println(users);

    }

}