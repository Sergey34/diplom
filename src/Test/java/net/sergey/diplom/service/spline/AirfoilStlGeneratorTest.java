package net.sergey.diplom.service.spline;

import org.junit.Test;

public class AirfoilStlGeneratorTest {
    @Test
    public void generate() throws Exception {
        String fileName = "/home/sergey/workspace/PycharmProjects/untitled1/n0012";
        String generate = new AirfoilStlGenerator().generate(100, fileName);
        System.out.println(generate);
    }

}