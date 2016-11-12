package net.sergey.diplom.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilAbstract;
import net.sergey.diplom.model.AirfoilDetail;
import net.sergey.diplom.model.AirfoilView;
import net.sergey.diplom.service.utils.UtilRoles;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ServiceImpl implements ServiceInt {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    //private final ApplicationContext applicationContext;
    private final DAO dao;

    @Autowired
    public ServiceImpl(DAO dao) {
        this.dao = dao;
    }

    /*@Autowired
    public ServiceImpl(ApplicationContext applicationContext, dao dao) {
        this.applicationContext = applicationContext;
        this.dao = dao;
    }*/

    @Override
    public List<Menu> getMenu() throws IOException {
        return dao.getAllMenu();
    }

    @Override
    public List<User> getUser(String name) {
        return dao.getUserByName(name);
    }

    @Override
    public boolean isValidUser(String name) {
        return getUser(name).size() == 0;
    }

    @Override
    public boolean addUser(User user) {
        try {
            dao.addUser(user);
            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("пользователь с именем {} уже существует в базе. {}", user.getUserName(), e.getStackTrace());
            return false;
        }
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return dao.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        UtilRoles.init(dao.getAllUserRoles());
    }

    @Override
    public void clean() {
        dao.cleanAllTables();
    }

    @Override
    public List<AirfoilAbstract> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        return Mapper.mappAirfoilOnAirfoilLink(dao.getAirfoilsWithLinksByPrefix(prefix, startNumber, count));

    }

    @Override
    public boolean updateAirfoil(AirfoilView airfoilView) {
        Airfoil airfoil = new Airfoil(airfoilView.getName(),
                airfoilView.getDescription(),
                airfoilView.getImage(),
                airfoilView.getId(),
                new Prefix(airfoilView.getPrefix().charAt(0)));
        dao.addAirfoils(airfoil);
        return false;
    }

    @Override
    public List<Airfoil> getAllAirfoils(int startNumber, int count) {
        return new ArrayList<>();
    }

    private static final Type TYPE_FILTER = new TypeToken<Map<String, List<Double>>>() {
    }.getType();
    @Autowired
    private ServletContext servletContext;

    @Override
    public AirfoilDetail getDetailInfo(int airfoilId) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (null == airfoil) {
            return AirfoilDetail.getAirfoilDetailError("airfoil не найден");
        }
        XYChart chartClCd = new XYChartBuilder().width(700).height(400).title("Cl v Cd").
                xAxisTitle("Cd").yAxisTitle("Cl").build();
        XYChart chartClAlpha = new XYChartBuilder().width(700).height(400).title("Cl v Alpha").
                xAxisTitle("Cd").yAxisTitle("Cl").build();
        XYChart chartCdAlpha = new XYChartBuilder().width(700).height(400).title("Alpha v Cd").
                xAxisTitle("Cd").yAxisTitle("Cl").build();
        XYChart chartCmAlpha = new XYChartBuilder().width(700).height(400).title("Alpha v Cm").
                xAxisTitle("Cd").yAxisTitle("Cl").build();
        XYChart chartClCdAlpha = new XYChartBuilder().width(700).height(400).title("Cl v Cd").
                xAxisTitle("Cd").yAxisTitle("Cl").build();

        fillXYChart(airfoil, chartClCd, chartClAlpha, chartCdAlpha, chartCmAlpha, chartClCdAlpha);
        String path = servletContext.getRealPath("/resources/chartTemp/");
        try {
            BitmapEncoder.saveBitmap(chartClCd, path + airfoil.getId() + "ClCd", BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmap(chartClAlpha, path + airfoil.getId() + "ClAlpha", BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmap(chartCdAlpha, path + airfoil.getId() + "CdAlpha", BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmap(chartCmAlpha, path + airfoil.getId() + "CmAlpha", BitmapEncoder.BitmapFormat.PNG);
            BitmapEncoder.saveBitmap(chartClCdAlpha, path + airfoil.getId() + "ClCdAlpha", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AirfoilDetail(airfoil);
    }

    private void fillXYChart(Airfoil airfoil, XYChart chartClCd, XYChart chartClAlpha, XYChart chartCdAlpha, XYChart chartCmAlpha, XYChart chartClCdAlpha) {
        for (Coordinates coordinates : airfoil.getCoordinates()) {
            Map<String, List<Double>> map = new Gson().fromJson(coordinates.getCoordinatesJson(), TYPE_FILTER);
            chartClCd.addSeries(coordinates.getFileName(), map.get("Cd"), map.get("Cl")).setMarker(SeriesMarkers.NONE);
            chartClAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), map.get("Cl")).setMarker(SeriesMarkers.NONE);
            List<Double> clDivCd = divListValue(map.get("Cl"), map.get("Cd"));
            chartClCdAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), clDivCd).setMarker(SeriesMarkers.NONE);
            chartCdAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), map.get("Cd")).setMarker(SeriesMarkers.NONE);
            chartCmAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), map.get("Cm")).setMarker(SeriesMarkers.NONE);
        }
    }

    private List<Double> divListValue(List<Double> cl, List<Double> cd) {
        ArrayList<Double> clDivCd = new ArrayList<>();
        for (int i = 0; i < cl.size(); i++) {
            clDivCd.add(cl.get(i) / cd.get(i));
        }
        return clDivCd;

    }
}
