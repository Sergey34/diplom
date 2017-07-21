import groovy.util.logging.Slf4j
import net.sergey.diplom.domain.menu.Menu
import net.sergey.diplom.domain.menu.MenuItem
import net.sergey.diplom.services.mainservice.EventService
import net.sergey.diplom.services.parser.ParserMenu
import net.sergey.diplom.services.parser.StringHandler
import net.sergey.diplom.services.parser.consts.Constant
import net.sergey.diplom.services.parser.siteconnection.ConnectionManager
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class ParserMenuScript  implements ParserMenu{
    private Constant constants
    private ConnectionManager connectionManager
    private EventService eventService
    private StringHandler stringHandler

    @Autowired
    ParserMenu(Constant constants, ConnectionManager connectionManager,
               EventService eventService, StringHandler stringHandler) {
        this.constants = constants
        this.connectionManager = connectionManager
        this.eventService = eventService
        this.stringHandler = stringHandler
    }

    List<Menu> parse(Collection<MenuItem> menuItemsInDB) throws IOException {
        eventService.clearProgressMap()
        eventService.updateProgress("menu", 0.0)
//        final List<String> airfoilMenu = new ArrayList<>();
        Element mmenu = connectionManager.getJsoupConnect(constants.HTTP_AIRFOIL_TOOLS_COM, constants.TIMEOUT).get().body().getElementsByClass(constants.MENU_CLASS_NAME).first()
        Elements menuList = mmenu.getElementsByTag(constants.MENU_LIST)
        Elements headerMenu = mmenu.getElementsByTag(constants.HEADER_MENU)
        List<Menu> menus = new ArrayList<>()
        eventService.updateProgress("menu", 20.0)
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i)
            Element element = headerMenu.get(i)
            if (constants.MENU_HEADER == element.text()) {
                Menu menu1 = Menu.builder().header(element.text()).build()
                List<MenuItem> menuItems = new ArrayList<>()
                menuItems.addAll(menuItemsInDB)
                Elements links = menuElement.getElementsByTag(constants.LINKS)
                for (Element link : links) {
                    Element a = link.getElementsByTag(constants.TEGA).first()
                    if (menu1.getHeader() == constants.MENU_HEADER) {
                        String text = stringHandler.createStringByPattern(a.text().trim(), constants.GET_MENU_TITLE_PATTERN)
                        String prefix = createPrefix(text)
                        MenuItem menuItem = MenuItem.builder().name(text).url(prefix).build()

                        if (prefix != constants.FILTER_ITEM) {
                            eventService.addKey(prefix)
//                            airfoilMenu.add(prefix);
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
        if (!"".equals(text)) {
            return String.valueOf(text.charAt(0))
        } else {
            return "allAirfoil"
        }
    }

}
