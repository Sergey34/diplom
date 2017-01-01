package parser.api;

import base.domain.airfoil.Airfoil;
import base.domain.airfoil.Prefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import parser.dao.AirfoilDao;
import parser.service.ParserService;

import java.util.HashSet;

@RestController
public class Controller {
    @Autowired
    private AirfoilDao airfoilDao;
    @Autowired
    private ParserService parserService;

    @RequestMapping("/create")
    public String create(String description, String name) {
        /*try {
            parserService.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return "{NOT_OK }"+e.toString();
        }*/
        Airfoil airfoil;
        try {
            airfoil = new Airfoil(name, description, new Prefix('A'), "asdfgh");
            airfoil.setCoordView("");
            airfoil.setCoordinates(new HashSet<>());
            airfoilDao.save(airfoil);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return "Error creating the user: " + ex.toString();
        }
        return "User succesfully created! (id = " + airfoil.getShortName() + ")";
        //return "{OK}";
    }
}
