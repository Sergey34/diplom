package parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import parser.service.ParserServiceInt;

@EntityScan("base")
@ComponentScan({"base/properties/", "parser"})
@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
public class Main {
    @Autowired
    private ParserServiceInt parserService;
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        log.info("ParserService is started");
    }
}

