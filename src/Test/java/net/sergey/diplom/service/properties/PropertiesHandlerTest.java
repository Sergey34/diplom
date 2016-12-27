package net.sergey.diplom.service.properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.io.FileInputStream;


public class PropertiesHandlerTest {
    @Test
    public void load() throws Exception {
        PropertiesHandler propertiesHandler = new PropertiesHandler();
        String s = "/home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/webapp/WEB-INF/config.properties";
        String s2 = "/home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/webapp/WEB-INF/config.properties";

        String md5Hex;
        try (FileInputStream fileInputStream = new FileInputStream(s)) {
            md5Hex = DigestUtils.md5Hex(fileInputStream);
        }
        String md5Hex2;
        try (FileInputStream fileInputStream = new FileInputStream(s2)) {
            md5Hex2 = DigestUtils.md5Hex(fileInputStream);
        }
        System.out.println(md5Hex);
        System.out.println(md5Hex.equals(md5Hex2));
        propertiesHandler.load(s);
        System.out.println(propertiesHandler.size());

    }

}