package net.sergey.diplom.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {
    @RequestMapping("/tt")
    public String home() {
        return "abousst";
    }
}
