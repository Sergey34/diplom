package net.sergey.diplom.service.properties;

import org.junit.Test;


public class PropertiesHandlerTest {
    @Test
    public void load() throws Exception {
        PropertiesHandler propertiesHandler = new PropertiesHandler();
        String s = "C:\\Users\\seko0716\\Desktop\\diplom\\diplom\\src\\main\\webapp\\WEB-INF\\config.properties";


        propertiesHandler.load(s);
        System.out.println(propertiesHandler.size());

    }

}