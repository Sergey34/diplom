package airfoilmanager.api;

import airfoilmanager.AirfoilManagerService.AirfoilConverter;
import airfoilmanager.AirfoilManagerService.ServiceAirfoil;
import base.domain.airfoil.Airfoil;
import base.domain.airfoil.Prefix;
import base.dto.AirfoilDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ControllerParser {

    @Autowired
    private ServiceAirfoil service;
    @Autowired
    private AirfoilConverter airfoilConverter;

    @RequestMapping("/getAirfoil")
    public Airfoil getAirfoil() {
        return service.getAirfoilById("a18-il");
    }

    @RequestMapping("/getAirfoilsByPrefix")
    public List<AirfoilDto> getAirfoilsByPrefix() {
        long start = System.currentTimeMillis();
        List<Airfoil> airfoilsByPrefix = service.getAirfoilsByPrefix(new Prefix('A'));
        List<AirfoilDto> airfoilsDto = airfoilsByPrefix.stream().map(airfoilConverter::toDtoAirfoil).collect(Collectors.toList());
        long stop = System.currentTimeMillis();
        log.debug("{}", stop - start);
        return airfoilsDto;
    }
}
