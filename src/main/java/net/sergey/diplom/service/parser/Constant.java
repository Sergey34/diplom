package net.sergey.diplom.service.parser;

import net.sergey.diplom.service.properties.PropertiesHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class Constant {


    public final Pattern GET_ID_BY_FULL_NAME_PATTERN;
    public final Pattern GET_FILE_NAME_BY_URL_PATTERN;
    public final Pattern GET_COUNT_PAGES_PATTERN;
    public final String REGEX;
    public final Pattern GET_MENU_TITLE_PATTERN;
    public final int TIMEOUT;
    public final String NO;
    public final String HTTP_AIRFOIL_TOOLS_COM;
    public final String MENU_CLASS_NAME;
    public final String MENU_LIST;
    public final String HEADER_MENU;
    public final String MENU_HEADER;
    public final String LINKS;
    public final String TEGA;
    public final String FILTER_ITEM;
    public final String AFSEARCHRESULT;
    public final String TR;
    public final String CELL12;
    public final String FILE_TYPE;
    public final String HREF;
    public final String CELL7;
    public final String MAX_CL_CD;
    public final String N_CRIT;
    public final String POLAR;
    public final String CELL2;

    @Autowired
    public Constant(PropertiesHandler propertiesHandler) {
        GET_ID_BY_FULL_NAME_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_ID_BY_FULL_NAME_PATTERN"));
        GET_FILE_NAME_BY_URL_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_FILE_NAME_BY_URL_PATTERN"));
        GET_COUNT_PAGES_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_COUNT_PAGES_PATTERN"));
        GET_MENU_TITLE_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_MENU_TITLE_PATTERN"));
        TIMEOUT = Integer.parseInt(propertiesHandler.getProperty("TIMEOUT"));
        REGEX = propertiesHandler.getProperty("REGEX");
        NO = propertiesHandler.getProperty("&no=");
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
        MAX_CL_CD = propertiesHandler.getProperty("cell4");
        N_CRIT = propertiesHandler.getProperty("cell3");
        POLAR = propertiesHandler.getProperty("polar");
        CELL2= propertiesHandler.getProperty("cell2");

    }
}
