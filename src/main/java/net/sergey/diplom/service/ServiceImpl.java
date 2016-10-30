package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Link;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.service.parser.UtilParser;
import net.sergey.diplom.service.utils.UtilRoles;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
    private static final String HTTP_AIRFOILTOOLS_COM = "http://airfoiltools.com/";
    //private final ApplicationContext applicationContext;
    private final DAO DAO;


    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    @Autowired
    public ServiceImpl(DAO dao) {
        DAO = dao;
    }

    /*@Autowired
    public ServiceImpl(ApplicationContext applicationContext, DAO DAO) {
        this.applicationContext = applicationContext;
        this.DAO = DAO;
    }*/


    @Override
    public List<Menu> getMenu() throws IOException {
        Element mmenu = Jsoup.connect(HTTP_AIRFOILTOOLS_COM).get().body().getElementsByClass("mmenu").get(0);
        Elements menuList = mmenu.getElementsByTag("ul");
        Elements headerMenu = mmenu.getElementsByTag("h3");
        ArrayList<Menu> menus = new ArrayList<>();
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i);
            Menu menu1 = new Menu();
            Elements element = headerMenu.get(i).getElementsByTag("h3");
            menu1.setHeader(element.text());
            Set<MenuItem> menuItems = new LinkedHashSet<>();
            Elements links = menuElement.getElementsByTag("li");
            for (Element link : links) {
                Element a = link.getElementsByTag("a").first();
                MenuItem menuItem = new MenuItem(a.text(), a.attr("href"));
                menuItems.add(menuItem);
            }
            menu1.setMenuItems(menuItems);
            menus.add(menu1);
        }

        try {
            DAO.addMenus(menus);
//            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Элемент меню: {} \n уже существует в базе {}", menus, e.getStackTrace());
//            return false;
        }


        return DAO.getAllMenu();
    }

    private int createString(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    @Override
    public Object getAirfoilsByPrefix(String prefix) throws IOException {
        List<Menu> menu = getMenu();
        String url = UtilParser.getUrlMenuByTitle(menu, prefix);
        String fullUrl = HTTP_AIRFOILTOOLS_COM + url.substring(0, url.length() - 1);


        Prefix prefix1 = new Prefix(prefix.charAt(0));
        int n = createString(Jsoup.connect(fullUrl + 0).get().html(), "Page 1 of ([0-9]+).+");
        for (int i = 0; i < n; i++) {
            Elements airfoilList = Jsoup.connect(fullUrl + i).get().body().getElementsByClass("afSearchResult").
                    first().getElementsByTag("tr");
            LOGGER.info(String.valueOf(i));
            for (int j = 0; j < airfoilList.size(); j += 2) {
                Elements cell12 = airfoilList.get(j).getElementsByClass("cell12");
                if (cell12.first() == null) {
                    j--;
                } else {
                    String name = cell12.text();
                    Elements links = airfoilList.get(j).getElementsByClass("cell3");
                    String image = airfoilList.get(j + 1).getElementsByClass("cell1").first().getElementsByTag("a").attr("href");
                    String description = airfoilList.get(j + 1).getElementsByClass("cell2").text();

                    Airfoil airfoil = new Airfoil(name, description, image);

                    airfoil.setLinks(parseLinks(links));

                    prefix1.addAirfoils(airfoil);


                }
            }
        }
        DAO.addPrefix(prefix1);
        return null;
    }

    private Set<Link> parseLinks(Elements links) {
        return links.first().getElementsByTag("a").stream().map(link
                -> new Link(link.text(), link.attr("href"))).collect(Collectors.toCollection(HashSet::new));

        /*HashSet<Link> linksSet = new HashSet<>();
        for (Element link : links.first().getElementsByTag("a")) {
            linksSet.add(new Link(link.text(), link.attr("href")));
        }
        return linksSet;*/
    }

    @Override
    public List<User> getUser(String name) {
        return DAO.getUserByName(name);
    }

    @Override
    public boolean isValidUser(String name) {
        return getUser(name).size() == 0;
    }

    @Override
    public boolean addUser(User user) {
        try {
            DAO.addUser(user);
            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("пользователь с именем {} уже существует в базе. {}", user.getUserName(), e.getStackTrace());
            return false;
        }
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return DAO.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        UtilRoles.init(DAO.getAllUserRoles());
    }

    @Override
    public void clean() {
        DAO.cleanAllTables();
    }
}
