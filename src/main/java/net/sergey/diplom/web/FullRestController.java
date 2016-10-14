package net.sergey.diplom.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;


@RequestMapping(value = "/rest")
@Controller
public class FullRestController {

    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public
    @ResponseBody
    HashMap<String, Integer> greeting() {
        System.out.println("###############");
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<String, Integer>();
        stringIntegerHashMap.put("qwe1", 123);
        stringIntegerHashMap.put("qwe2", 1232);
        stringIntegerHashMap.put("qwe3", 1233);
        stringIntegerHashMap.put("qwe4", 1235);
        return stringIntegerHashMap;
    }

    @RequestMapping(value = "/greeting3", method = RequestMethod.GET)
    public
    @ResponseBody
    HashMap<String, Integer> greeting3() {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<String, Integer>();
        stringIntegerHashMap.put("qwe1", 123);
        stringIntegerHashMap.put("qwe2", 1232);
        stringIntegerHashMap.put("qwe3", 1233);
        stringIntegerHashMap.put("qwe4", 1235);
        return stringIntegerHashMap;
    }
}
