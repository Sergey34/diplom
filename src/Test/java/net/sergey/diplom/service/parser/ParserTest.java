package net.sergey.diplom.service.parser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ParserTest {
    @Autowired
    Parser parser;

    @Test
    public void initTest() throws Exception {
        Assert.assertTrue(parser != null);
        Assert.assertTrue(parser.dao != null);
        parser.getAirfoilsByPrefix("A a18 to avistar (88)");
    }

    @Test
    public void initTest2() throws Exception {
        parser.parseMenu();
        System.out.println(Integer.MAX_VALUE);
    }



}