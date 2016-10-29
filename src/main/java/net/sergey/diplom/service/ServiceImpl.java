package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.MenuItem;
import net.sergey.diplom.domain.User;
import net.sergey.diplom.domain.UserRole;
import net.sergey.diplom.service.utils.UtilParser;
import net.sergey.diplom.service.utils.UtilRoles;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                menuItem.setId(menuItem.hashCode());
                menuItems.add(menuItem);
            }
            menu1.setMenuItems(menuItems);
            menu1.setId(i + 1);
            menus.add(menu1);
        }

        DAO.addMenus(menus);

        List<Menu> allMenu = DAO.getAllMenu();
        return allMenu;
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
    public Object getAirfoilsByLiteral(String literal) throws IOException {
        List<Menu> menu = getMenu();
        String url = UtilParser.getUrlMenuByTitle(menu, literal);
        String fullUrl = HTTP_AIRFOILTOOLS_COM + url.substring(0, url.length() - 1);
        int n = createString(Jsoup.connect(fullUrl + 0).get().html(), "Page 1 of ([0-9]+).+");
        for (int i = 0; i < n; i++) {
            Elements tr = Jsoup.connect(fullUrl + i).get().body().getElementsByClass("afSearchResult").
                    first().getElementsByTag("tr");
            LOGGER.info(String.valueOf(i));
            for (int j = 0; j < tr.size(); j += 2) {
                Elements cell12 = tr.get(j).getElementsByClass("cell12");
                if (cell12.first() == null) {
                    j--;
                } else {
                    String name = cell12.text();
                    Elements links = tr.get(j).getElementsByClass("cell3");
                    String image = tr.get(j + 1).getElementsByClass("cell1").first().getElementsByTag("a").attr("href");
                    String description = tr.get(j + 1).getElementsByClass("cell2").text();


                    LOGGER.info(name);
                    LOGGER.info(String.valueOf(links));
                    LOGGER.info(image);
                    LOGGER.info(description);
                }
            }
        }
        return null;
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
    public void addUser(User user) {
        DAO.addUser(user);
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return DAO.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        UtilRoles.init(DAO.getAllUserRoles());
    }
}
