package net.sergey.diplom.dao;

import net.sergey.diplom.domain.User;
import net.sergey.diplom.domain.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class DAOImplTest {
    @Test
    public void getProfilesByPrefix() throws Exception {

    }

    @Test
    public void getProfilesByName() throws Exception {

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
        dao.addUser(user);
    }

    @Test
    public void updateUserPassword() throws Exception {

    }

    @Autowired
    private DAO dao;


    @Test
    public void getUserById() throws Exception {
        List<User> users = dao.getUserByName("alex");
        System.out.println(users);

    }

}