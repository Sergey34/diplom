package net.sergey.diplom.services.parser;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.services.EventService;
import net.sergey.diplom.services.utils.UtilsLogger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sergey.diplom.services.parser.ConstantApi.GET_COORDINATE_VIEW;
import static net.sergey.diplom.services.parser.ParserService.getJsoupConnect;
import static net.sergey.diplom.services.parser.ParserService.isDoubleStr;

@Scope("prototype")
@Component
public class ParserAirfoil implements Callable<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final String ONCLICK = "onclick";
    private static final String HTTP_M_SELIG_AE_ILLINOIS_EDU_ADS_COORD_DATABASE_HTML = "http://m-selig.ae.illinois.edu/ads/coord_database.html";
    private final DAO dao;
    private final EventService eventService;
    private final Constant constants;
    private final ParseFileScv parseFileScv;
    private String prefix;

    @Autowired
    public ParserAirfoil(DAO dao, EventService eventService, Constant constants, ParseFileScv parseFileScv) {
        this.dao = dao;
        this.eventService = eventService;
        this.constants = constants;
        this.parseFileScv = parseFileScv;
    }

    @Override
    public Void call() throws Exception {
        parseAirfoilByUrl(prefix);
        return null;
    }

    private void parseAirfoilByUrl(String prefix) throws IOException {
        String url = ConstantApi.GET_LIST_AIRFOIL_BY_PREFIX + prefix + constants.NO;
        Prefix prefix1 = new Prefix(prefix.charAt(0));
        int countPages = createIntByPattern(getJsoupConnect(url, constants.TIMEOUT).get().html(), constants.GET_COUNT_PAGES_PATTERN);
        for (int i = 0; i < countPages; i++) {
            Elements airfoilList = getJsoupConnect(url + i, constants.TIMEOUT).get().body().getElementsByClass(constants.AFSEARCHRESULT).
                    first().getElementsByTag(constants.TR);
            List<Airfoil> airfoils = parsePage(prefix1, airfoilList, countPages);
            dao.addAirfoils(airfoils);
        }
        eventService.updateProgress(prefix, 100.0);
    }


    private List<Airfoil> parsePage(Prefix prefix1, Elements airfoilList, int countPages) throws IOException {
        List<Airfoil> airfoils = new ArrayList<>();
        for (int j = 0; j < airfoilList.size(); j += 2) {
            Elements cell12 = airfoilList.get(j).getElementsByClass(constants.CELL12);
            if (cell12.first() == null) {
                j--;//фильтруем реламу
            } else {
                String name = cell12.text();
                String idAirfoil = ParserService.createStringByPattern(name, constants.GET_ID_BY_FULL_NAME_PATTERN);
                Airfoil airfoil = parseAirfoilById(idAirfoil);
                airfoils.add(airfoil);
                String key = String.valueOf(prefix1.getPrefix());
                double value = eventService.getProgressValueByKey(key) + (90.0 / countPages / airfoilList.size());
                eventService.updateProgress(key, value);
            }
        }
        return airfoils;
    }

    private String parseCoordinateView(String shortName) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(new URL(GET_COORDINATE_VIEW + shortName).openStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.trim().split(" +");
            if (isDoubleStr(split[0]) && isDoubleStr(split[split.length - 1])) {
                stringBuilder.append(split[0]).append(",").append(split[split.length - 1]).append('\n');
            }
        }
        return stringBuilder.toString();
    }

    private int createIntByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private Set<Coordinates> downloadDetailInfo(Element detail) throws IOException {
        Elements polar = detail.getElementsByClass(constants.POLAR);
        if (polar.size() == 0) {
            return Collections.emptySet();
        }
        polar = polar.first().getElementsByTag(constants.TR);
        Set<Coordinates> coordinates = new HashSet<>();
        for (Element element : polar) {
            Element reynolds = element.getElementsByClass(constants.REYNOLDS).first();
            Element nCrit = element.getElementsByClass(constants.N_CRIT).first();
            Element maxClCd = element.getElementsByClass(constants.MAX_CL_CD).first();
            Element cell7 = element.getElementsByClass(constants.CELL7).first();
            if (cell7 != null) {
                Elements a = cell7.getElementsByTag(constants.TEGA);
                if (a.size() != 0) {
                    String fileName = ParserService.createStringByPattern(a.attr(constants.HREF), constants.GET_FILE_NAME_BY_URL_PATTERN);
                    URL urlFile = new URL(ConstantApi.GET_FILE_CSV + fileName);
                    LOGGER.debug("url {}{}", ConstantApi.GET_FILE_CSV, fileName);
                    Coordinates coordinateItem = new Coordinates(parseFileScv.csvToString(urlFile.openStream()), fileName + constants.FILE_TYPE);
                    coordinateItem.setRenolgs(reynolds.text());
                    coordinateItem.setNCrit(nCrit.text());
                    coordinateItem.setMaxClCd(maxClCd.text());
                    coordinates.add(coordinateItem);
                }
            }
        }
        return coordinates;
    }

    private Airfoil parseAirfoilById(String airfoilId) throws IOException {
        Element detail = ParserService.getJsoupConnect(ConstantApi.GET_DETAILS + airfoilId, constants.TIMEOUT).get().getElementById("content");
        String name = detail.getElementsByTag("h1").get(0).text();
        String description = filterDescription(detail, airfoilId).html();
        String coordinateView = parseCoordinateView(airfoilId);
        Airfoil airfoil = new Airfoil(name, description, airfoilId);
        Set<Coordinates> coordinates = downloadDetailInfo(detail);
        airfoil.setCoordView(coordinateView);
        airfoil.setCoordinates(coordinates);
        return airfoil;
    }

    private Element filterDescription(Element detail, String airfoilId) {
        Element descriptionFull = detail.getElementsByClass(constants.DESCRIPTION).get(0);
        for (Element a : descriptionFull.getElementsByTag("a")) {
            if ("UIUC Airfoil Coordinates Database".equals(a.text())) {
                replaceUrl(a, HTTP_M_SELIG_AE_ILLINOIS_EDU_ADS_COORD_DATABASE_HTML);
                continue;
            }
            if ("Source dat file".equals(a.text())) {
                replaceUrl(a, "/files/airfoil_img/" + airfoilId + ".dat");
                continue;
            }
            a.remove();
        }
        return descriptionFull;
    }

    private void replaceUrl(Element a, String attributeValue) {
        a.attr("href", attributeValue);
        a.removeAttr(ONCLICK);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
