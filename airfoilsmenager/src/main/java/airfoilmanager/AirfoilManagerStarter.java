package airfoilmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EntityScan("base")
@ComponentScan({"base/properties/", "airfoilmanager"})
@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
public class AirfoilManagerStarter {
    public static void main(String[] args) {
        SpringApplication.run(AirfoilManagerStarter.class, args);
        log.info("ParserService is started");
    }
}
