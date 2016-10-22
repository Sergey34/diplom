package net.sergey.diplom.dao;

import net.sergey.diplom.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class DAOImplTest {

    @Autowired
    private DAO dao;


    @Test
    public void getUserById() throws Exception {
        List<User> users = dao.getUserById("alex");
        System.out.println(users);

    }

}