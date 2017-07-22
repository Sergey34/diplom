import groovy.util.logging.Slf4j
import net.sergey.diplom.dao.airfoil.DaoAirfoil
import net.sergey.diplom.dao.airfoil.DaoCharacteristics
import net.sergey.diplom.domain.airfoil.Airfoil
import net.sergey.diplom.domain.airfoil.Characteristics
import net.sergey.diplom.services.mainservice.EventService
import net.sergey.diplom.services.parser.ParseFileScv
import net.sergey.diplom.services.parser.ParserAirfoil
import net.sergey.diplom.services.parser.StringHandler
import net.sergey.diplom.services.parser.consts.ConstantApi
import net.sergey.diplom.services.parser.siteconnection.ConnectionManager
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.dao.DuplicateKeyException

import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Matcher
import java.util.regex.Pattern

import static net.sergey.diplom.services.parser.consts.ConstantApi.GET_COORDINATE_VIEW

@Slf4j
class ParserAirfoilScript implements ParserAirfoil {
    private static String ONCLICK = "onclick"
    private static
    final String HTTP_M_SELIG_AE_ILLINOIS_EDU_ADS_COORD_DATABASE_HTML = "http://m-selig.ae.illinois.edu/ads/coord_database.html"
    private static volatile AtomicBoolean finish
    public static final int TIMEOUT = 10000
    private EventService eventService
    private ParseFileScv parseFileScv
    private ConnectionManager connectionManager
    private StringHandler stringHandler
    private DaoAirfoil daoAirfoil
    private DaoCharacteristics daoCharacteristics
    private String prefix

    @Autowired
    ParserAirfoilImpl(EventService eventService, @Qualifier("parser_service") ParseFileScv parseFileScv,
                      ConnectionManager connectionManager, StringHandler stringHandler, DaoAirfoil daoAirfoil, DaoCharacteristics daoCharacteristics) {
        this.eventService = eventService
        this.parseFileScv = parseFileScv
        this.connectionManager = connectionManager
        this.stringHandler = stringHandler
        this.daoAirfoil = daoAirfoil
        this.daoCharacteristics = daoCharacteristics
        finish = new AtomicBoolean(false)
    }

    @Override
    void setFinish() {
        finish = new AtomicBoolean(true)
    }

    @Override
    Void call() throws Exception {
        try {
            parseAirfoilByUrl(prefix)
        } catch (Exception e) {
            log.warn(e.getMessage(), e)
        }
        return null
    }

    private void parseAirfoilByUrl(String prefix) {
        String url = ConstantApi.GET_LIST_AIRFOIL_BY_PREFIX + prefix + '&no='
        char prefix1 = prefix.charAt(0)
        int countPages = createIntByPattern(connectionManager.getJsoupConnect(url, TIMEOUT).get().html(), Pattern.compile('Page 1 of ([0-9]+).+'))
        for (int i = 0; i < countPages; i++) {
            if (finish.get()) {
                return
            }
            Elements airfoilList = connectionManager.getJsoupConnect(url + i, TIMEOUT).get().body().getElementsByClass("afSearchResult").
                    first().getElementsByTag("tr")
            List<Airfoil> airfoils = parsePage(prefix1, airfoilList, countPages)
            try {
                for (Airfoil airfoil : airfoils) {
                    daoCharacteristics.save(airfoil.getCharacteristics())
                }
                daoAirfoil.save(airfoils)
            } catch (DuplicateKeyException ignored) {
                log.trace("уже существует", ignored)
            }
        }
        eventService.updateProgress(prefix, 100.0)
    }

    private List<Airfoil> parsePage(char prefix, Elements airfoilList, int countPages) {
        List<Airfoil> airfoils = new ArrayList<>()
        for (int j = 0; j < airfoilList.size(); j += 2) {
            Elements cell12 = airfoilList.get(j).getElementsByClass("cell12")
            if (!cell12.first()) {
                j--//фильтруем реламу
            } else {
                String name = cell12.text()
                String idAirfoil = stringHandler.createStringByPattern(name, Pattern.compile('\\(([a-zA-Z0-9_-]+)\\) .*'))
                Airfoil airfoil = parseAirfoilById(idAirfoil)
                airfoils.add(airfoil)
                String key = String.valueOf(prefix)
                double value = eventService.getProgressValueByKey(key) + (90.0 / countPages / airfoilList.size())
                eventService.updateProgress(key, value)
            }
        }
        return airfoils
    }

    private String parseCoordinateView(String shortName) {
        StringBuilder stringBuilder = new StringBuilder()
        new BufferedReader(new InputStreamReader(new URL(GET_COORDINATE_VIEW + shortName).openStream())).eachLine { line ->
            String[] split = line.trim().split(" +")
            if (stringHandler.isDoubleStr(split[0]) && stringHandler.isDoubleStr(split[split.length - 1])) {
                stringBuilder.append(split[0]).append(",").append(split[split.length - 1]).append('\n')
            }
        }
        return stringBuilder.toString()
    }

    private int createIntByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item)
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1))
        }
        return 0
    }

    private Set<Characteristics> downloadDetailInfo(Element detail) throws IOException {
        Elements polar = detail.getElementsByClass("polar")
        if (polar.size() == 0) {
            return Collections.emptySet()
        }
        polar = polar.first().getElementsByTag("tr")
        Set<Characteristics> characteristics = new HashSet<>()
        for (Element element : polar) {
            Element reynolds = element.getElementsByClass('cell2').first()
            Element nCrit = element.getElementsByClass("cell3").first()
            Element maxClCd = element.getElementsByClass("cell4").first()
            Element cell7 = element.getElementsByClass("cell7").first()
            if (cell7) {
                Elements a = cell7.getElementsByTag("a")
                if (a.size() != 0) {
                    String fileName = stringHandler.createStringByPattern(a.attr("href"), Pattern.compile('polar=(.+)$'))
                    URL urlFile = new URL(ConstantApi.GET_FILE_CSV + fileName)
                    log.debug("url {}{}", ConstantApi.GET_FILE_CSV, fileName)
                    Characteristics coordinateItem = Characteristics.builder()
                            .coordinatesStl(parseFileScv.csvToString(urlFile.openStream())).fileName(fileName + ".csv").build()
                    coordinateItem.setRenolds(reynolds.text().replace(",", "") as double)
                    coordinateItem.setNCrit((nCrit.text() ?: "0.0") as double)
                    coordinateItem.setMaxClCd((maxClCd.text().find(/^[\d\.]+/) ?: "0.0") as double)
                    coordinateItem.setAlpha(maxClCd.text().find(/\d+.\d+°/))// Pattern.compile('.+ at α=(.+°)$'))
                    characteristics.add(coordinateItem)
                }
            }
        }
        return characteristics
    }

    private Airfoil parseAirfoilById(String airfoilId) throws IOException {
        Element detail = connectionManager.getJsoupConnect(ConstantApi.GET_DETAILS + airfoilId, TIMEOUT).get().getElementById("content")
        String name = detail.getElementsByTag("h1").get(0).text()
        String description = filterDescription(detail, airfoilId).html()
        String coordinateView = parseCoordinateView(airfoilId)
        Airfoil airfoil = Airfoil.builder().name(name).description(description).shortName(airfoilId).prefix(airfoilId.toUpperCase().charAt(0)).build()
        Set<Characteristics> characteristics = downloadDetailInfo(detail)
        airfoil.setCoordView(coordinateView)
        airfoil.setCharacteristics(characteristics)
        return airfoil
    }

    private Element filterDescription(Element detail, String airfoilId) {
        Element descriptionFull = detail.getElementsByClass("cell1").get(0)
        for (Element a : descriptionFull.getElementsByTag("a")) {
            if ("UIUC Airfoil Coordinates Database" == a.text()) {
                replaceUrl(a, HTTP_M_SELIG_AE_ILLINOIS_EDU_ADS_COORD_DATABASE_HTML)
                continue
            }
            if ("Source dat file" == a.text()) {
                replaceUrl(a, "/files/airfoil_img/${airfoilId}.dat")
                continue
            }
            a.remove()
        }
        return descriptionFull
    }

    private void replaceUrl(Element a, String attributeValue) {
        a.attr("href", attributeValue)
        a.removeAttr(ONCLICK)
    }

    @Override
    void setPrefix(String prefix) {
        this.prefix = prefix
    }
}
