package parser.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import parser.service.ParserServiceInt;

@RestController
@Slf4j
public class ControllerParser {
    @Autowired
    private ParserServiceInt parserService;

    @RequestMapping("/initDB")
    public String create() {
        return parserService.parse();
    }

    @RequestMapping("/stop")
    public String stopParsing() {
        parserService.stop();
        return "Парсинг остановлен";
    }

    @RequestMapping("/parsePrefix/{prefix}")
    public String parsePrefix(@PathVariable String prefix) {
        return parserService.parsePrefix(prefix);
    }

    @RequestMapping("/parseAirfoil/{airfoilId}")
    public String parseAirfoil(@PathVariable String airfoilId) {
        return parserService.parseAirfoil(airfoilId);
    }


}
