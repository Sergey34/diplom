package net.sergey.diplom.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


@RequestMapping(value = "/rest")
@RestController
public class FullRestController {

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public HashMap<String, Integer> greeting() throws IOException, InterruptedException {
        String args = "aargs";
        Process p = Runtime.getRuntime().exec("python3 /home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/resources/myscript.py " + args);
        int i = p.waitFor();
        System.out.println(i);

        File file = new File("/home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/resources/" + args + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = bufferedReader.readLine();
        System.out.println(s);

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put("qwe1", 123);
        stringIntegerHashMap.put("qwe2", 1232);
        stringIntegerHashMap.put("qwe3", 1233);
        stringIntegerHashMap.put("qwe4", 1235);
        return stringIntegerHashMap;
    }

    @RequestMapping(value = "/greeting3", method = RequestMethod.GET)
    public HashMap<String, Integer> greeting3() {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<String, Integer>();
        stringIntegerHashMap.put("qwe1", 123);
        stringIntegerHashMap.put("qwe2", 1232);
        stringIntegerHashMap.put("qwe3", 1233);
        stringIntegerHashMap.put("qwe4", 1235);
        return stringIntegerHashMap;
    }
}
