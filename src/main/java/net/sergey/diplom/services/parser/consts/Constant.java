package net.sergey.diplom.services.parser.consts;

import net.sergey.diplom.services.properties.PropertiesHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
@Component
public class Constant {
    private final PropertiesHandler propertiesHandler;
    public String DESCRIPTION;
    public Pattern GET_ID_BY_FULL_NAME_PATTERN;
    public Pattern GET_MAXCLCD_PATTERN = Pattern.compile("^(.+) at .+");//63.6 at α=3.75°
    public Pattern GET_ALPHA_PATTERN = Pattern.compile(".+ at (α=.+°)$");
    public Pattern GET_FILE_NAME_BY_URL_PATTERN;
    public Pattern GET_COUNT_PAGES_PATTERN;
    public String REGEX;
    public Pattern GET_MENU_TITLE_PATTERN;
    public int TIMEOUT;
    public String NO;
    public String HTTP_AIRFOIL_TOOLS_COM;
    public String MENU_CLASS_NAME;
    public String MENU_LIST;
    public String HEADER_MENU;
    public String MENU_HEADER;
    public String LINKS;
    public String TEGA;
    public String FILTER_ITEM;
    public String AFSEARCHRESULT;
    public String TR;
    public String CELL12;
    public String FILE_TYPE;
    public String HREF;
    public String CELL7;
    public String MAX_CL_CD;
    public String N_CRIT;
    public String POLAR;
    public String REYNOLDS;

    @Autowired
    public Constant(PropertiesHandler propertiesHandler) {
        this.propertiesHandler = propertiesHandler;
    }

    public void initConst() {
        GET_ID_BY_FULL_NAME_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_ID_BY_FULL_NAME_PATTERN"));
        GET_FILE_NAME_BY_URL_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_FILE_NAME_BY_URL_PATTERN"));
        GET_COUNT_PAGES_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_COUNT_PAGES_PATTERN"));
        GET_MENU_TITLE_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_MENU_TITLE_PATTERN"));
        TIMEOUT = Integer.parseInt(propertiesHandler.getProperty("TIMEOUT"));
        REGEX = propertiesHandler.getProperty("REGEX");
        NO = propertiesHandler.getProperty("no");
        HTTP_AIRFOIL_TOOLS_COM = propertiesHandler.getProperty("HTTP_AIRFOIL_TOOLS_COM");
        MENU_CLASS_NAME = propertiesHandler.getProperty("menu_class_name");
        MENU_LIST = propertiesHandler.getProperty("menuList");
        HEADER_MENU = propertiesHandler.getProperty("headerMenu");
        LINKS = propertiesHandler.getProperty("links");
        TEGA = propertiesHandler.getProperty("tegA");
        MENU_HEADER = propertiesHandler.getProperty("menu_Header");
        FILTER_ITEM = propertiesHandler.getProperty("filterItem");
        AFSEARCHRESULT = propertiesHandler.getProperty("afSearchResult");
        TR = propertiesHandler.getProperty("tr");
        CELL12 = propertiesHandler.getProperty("cell12");
        FILE_TYPE = propertiesHandler.getProperty(".csv");
        HREF = propertiesHandler.getProperty("href");
        CELL7 = propertiesHandler.getProperty("cell7");
        MAX_CL_CD = propertiesHandler.getProperty("maxClCd");
        N_CRIT = propertiesHandler.getProperty("nCrit");
        POLAR = propertiesHandler.getProperty("polar");
        REYNOLDS = propertiesHandler.getProperty("reynolds");
        DESCRIPTION = propertiesHandler.getProperty("description");
        ConstantApi.GET_FILE_CSV = propertiesHandler.getProperty("GET_FILE_CSV");
        ConstantApi.GET_DETAILS = propertiesHandler.getProperty("GET_DETAILS");
        ConstantApi.GET_LIST_AIRFOIL_BY_PREFIX = propertiesHandler.getProperty("GET_LIST_AIRFOIL_BY_PREFIX");
        ConstantApi.GET_COORDINATE_VIEW = propertiesHandler.getProperty("GET_COORDINATE_VIEW");
    }
}
