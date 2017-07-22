import groovy.util.logging.Slf4j
import net.sergey.diplom.domain.menu.Menu
import net.sergey.diplom.domain.menu.MenuItem
import net.sergey.diplom.services.mainservice.EventService
import net.sergey.diplom.services.parser.ParserMenu
import net.sergey.diplom.services.parser.StringHandler
import net.sergey.diplom.services.parser.siteconnection.ConnectionManager
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired

import java.util.regex.Pattern

@Slf4j
class ParserMenuScript implements ParserMenu {

    private ConnectionManager connectionManager
    private EventService eventService
    private StringHandler stringHandler

    @Autowired
    ParserMenu(ConnectionManager connectionManager, EventService eventService, StringHandler stringHandler) {
        this.connectionManager = connectionManager
        this.eventService = eventService
        this.stringHandler = stringHandler
    }

    List<Menu> parse(Collection<MenuItem> menuItemsInDB) throws IOException {
        eventService.clearProgressMap()
        eventService.updateProgress("menu", 0.0)
        Element mmenu = connectionManager.getJsoupConnect("http://airfoiltools.com/", 10000).get().body()
                .getElementsByClass("mmenu").first()
        Elements menuList = mmenu.getElementsByTag("ul")
        Elements headerMenu = mmenu.getElementsByTag("h3")
        List<Menu> menus = new ArrayList<>()
        eventService.updateProgress("menu", 20.0)
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i)
            Element element = headerMenu.get(i)
            if ("Airfoils A to Z" == element.text()) {
                Menu menu1 = Menu.builder().header(element.text()).build()
                List<MenuItem> menuItems = new ArrayList<>()
                menuItems.addAll(menuItemsInDB)
                Elements links = menuElement.getElementsByTag("li")
                for (Element link : links) {
                    Element a = link.getElementsByTag("a").first()
                    if (menu1.getHeader() == "Airfoils A to Z") {
                        String text = stringHandler.createStringByPattern(a.text().trim(), Pattern.compile('^(.+) \\([0-9]*\\)$'))
                        String prefix = createPrefix(text)
                        MenuItem menuItem = MenuItem.builder().name(text).url(prefix).build()
                        if (prefix != "allAirfoil") {
                            eventService.addKey(prefix)
                            if (menuItems.contains(menuItem)) {
                                menuItems.remove(menuItem)
                            }
                            menuItems.add(menuItem)
                            eventService.updateProgress("menu", eventService.getProgressValueByKey("menu") + 70.0 / links.size())
                        }
                    }
                }
                menu1.setItems(menuItems)
                menus.add(menu1)
                break
            }
        }
        eventService.updateProgress("menu", 100.0)
        return menus
    }


    private String createPrefix(String text) {
        if ("" != text) {
            return String.valueOf(text.charAt(0))
        } else {
            return "allAirfoil"
        }
    }

}
