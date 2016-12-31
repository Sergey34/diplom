package parser.api;

import base.domain.airfoil.Airfoil;
import base.domain.airfoil.Prefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import parser.dao.AirfoilDao;

import java.util.HashSet;

@RestController
public class Controller {
    @Autowired
    AirfoilDao airfoilDao;

    @RequestMapping("/create")
    public String create(String description, String name) {
        Airfoil airfoil = null;
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
    }
}
