package parser.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class MainController {
    @RequestMapping("/")
    @ResponseBody
    public String index() {
        log.debug("qweqweqweq");
        log.error("qweqweqweq");
        log.warn("qweqweqweq");
        log.info("qweqweqweq");
        log.trace("qweqweqweq");
        return "OK";
    }

}