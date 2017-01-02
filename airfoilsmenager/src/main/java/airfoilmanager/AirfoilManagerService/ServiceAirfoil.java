package airfoilmanager.AirfoilManagerService;

import airfoilmanager.dao.AirfoilDao;
import airfoilmanager.dao.MenuDao;
import base.domain.airfoil.Airfoil;
import base.domain.airfoil.Prefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceAirfoil {
    @Autowired
    MenuDao menuDao;
    @Autowired
    AirfoilDao airfoilDao;


    public Airfoil getAirfoilById(String id) {
        return airfoilDao.findOne(id);
    }

    public List<Airfoil> getAirfoilsByPrefix(Prefix prefix) {
        return airfoilDao.findAllByPrefix(prefix);
    }
}
