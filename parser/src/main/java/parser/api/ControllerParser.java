package parser.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import parser.service.ParserService;

@RestController
@Slf4j
public class ControllerParser {
    @Autowired
    private ParserService parserService;

    @RequestMapping("/create")
    public String create(String description, String name) {
        try {
            parserService.parse();
        } catch (Exception e) {
            log.warn("Ошибка {}", e);
            return "{NOT_OK }"+e.toString();
        }
        return "{OK}";
    }
}
