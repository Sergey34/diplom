package parser.service;


import base.UtilsLogger;
import base.domain.airfoil.Airfoil;
import base.domain.airfoil.Coordinates;
import base.domain.airfoil.Prefix;
import lombok.NonNull;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import parser.dao.AirfoilDao;
import parser.service.constants.Constant;
import parser.service.constants.ConstantApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Scope("prototype")
@Component
public class ParserAirfoil implements Callable<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final Constant constants;
    private final AirfoilDao airfoilDao;
    private String prefix;

    @Autowired
    public ParserAirfoil(Constant constants, AirfoilDao airfoilDao) {
        this.airfoilDao = airfoilDao;
        this.constants = constants;
    }

    private static String csvToString(@NonNull InputStream urlFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();
        }
    }

    @Override
    public Void call() throws Exception {
        parseAirfoilsByPrefix(prefix);
        return null;
    }

    private void parseAirfoilsByPrefix(@NonNull String prefix) throws IOException {
        String url = ConstantApi.GET_LIST_AIRFOIL_BY_PREFIX + prefix + constants.NO;
        Prefix prefix1 = new Prefix(prefix.charAt(0));
        int countPages = createIntByPattern(ParserService.getJsoupConnect(url, constants.TIMEOUT).get().html(), constants.GET_COUNT_PAGES_PATTERN);
        for (int i = 0; i < countPages; i++) {
            Elements airfoilList = ParserService.getJsoupConnect(url + i, constants.TIMEOUT).get().body().getElementsByClass(constants.AFSEARCHRESULT).
                    first().getElementsByTag(constants.TR);
            List<Airfoil> airfoils = parsePage(prefix1, airfoilList, countPages);
            airfoilDao.save(airfoils);
        }
        // eventService.updateProgress(prefix, 100.0);
    }


    private List<Airfoil> parsePage(@NonNull Prefix prefix1, @NonNull Elements airfoilList, int countPages) throws IOException {
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
                //double value = eventService.getProgressValueByKey(key) + (90.0 / countPages / airfoilList.size());
                // eventService.updateProgress(key, value);
            }
        }
        return airfoils;
    }

    private String parseCoordinateView(@NonNull String shortName) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(new URL(ConstantApi.GET_COORDINATE_VIEW + shortName).openStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.trim().split(" +");
            if (ParserService.isDoubleStr(split[0]) && ParserService.isDoubleStr(split[split.length - 1])) {
                stringBuilder.append(split[0]).append(",").append(split[split.length - 1]).append('\n');
            }
        }
        return stringBuilder.toString();
    }

    private int createIntByPattern(@NonNull String item, @NonNull Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private Set<Coordinates> downloadDetailInfo(@NonNull Element detail) throws IOException {
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
                    Coordinates coordinateItem = new Coordinates(csvToString(urlFile.openStream()), fileName + constants.FILE_TYPE);
                    coordinateItem.setRenolds(reynolds.text());
                    coordinateItem.setNCrit(nCrit.text());
                    coordinateItem.setMaxClCd(maxClCd.text());
                    coordinates.add(coordinateItem);
                }
            }
        }
        return coordinates;
    }

    public void setPrefix(@NonNull String prefix) {
        this.prefix = prefix;
    }

    private Airfoil parseAirfoilById(@NonNull String airfoilId) throws IOException {
        Element detail = ParserService.getJsoupConnect(ConstantApi.GET_DETAILS + airfoilId, constants.TIMEOUT).get().getElementById("content");
        String name = detail.getElementsByTag("h1").get(0).text();
        String description = detail.getElementsByClass("cell1").get(0).html();
        String coordinateView = parseCoordinateView(airfoilId);
        Airfoil airfoil = new Airfoil(name, description, new Prefix(airfoilId.charAt(0)), airfoilId);
        airfoil.setCoordView(coordinateView);
        Set<Coordinates> coordinates = downloadDetailInfo(detail);
        airfoil.setCoordinates(coordinates);
        return airfoil;
    }

    public void addAirfoilById(@NonNull String airfoilId) throws IOException {
        Airfoil airfoil = parseAirfoilById(airfoilId);
        airfoilDao.save(airfoil);
    }
}
