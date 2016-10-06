package net.sergey.diplom.service;

import com.google.gson.Gson;
import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.domain.ParserImplements;
import net.sergey.diplom.domain.SettingLoadJSON;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class AggregatorServiceImpl implements AggregatorService {

    @Autowired
    private DAO DAO;

    @Transactional
    public void saveSettings(Filter filter) {
        DAO.saveSettings(filter);
    }

    @Transactional
    public Settings loadSetting(String SQL) {
        List<SettingLoadJSON> settingJSONList = DAO.loadSetting(SQL);
        String settingJSON;
        if (settingJSONList.size() > 0) {
            settingJSON = DAO.loadSetting(SQL).get(0).getFilterJson();
            return jsonToObject(settingJSON);
        }
        return new Settings();
    }

    @Transactional
    public String showData() {
        Object name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SettingLoadJSON> filtersJSINList =
                DAO.loadSetting("SELECT atribJson FROM filter WHERE username='" + name + "' ");
        List<ParserImplements> parsersImplementation =
                DAO.loadParsersImplementation("SELECT Implementation FROM parsers ");
        if (filtersJSINList.size() > 0) {
            String result = "";
            ClassPathXmlApplicationContext context =
                    new ClassPathXmlApplicationContext("/WEB-INF/spring/root-context.xml");
            for (ParserImplements parserImplementation : parsersImplementation) {
                Parser parser = (Parser) context.getBean(parserImplementation.getParserImplements());
                try {
                    result += parser.getData(filtersJSINList.get(0).getFilterJson()) + "</br>";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
        return "нет информации по запрсу";//ничего не напарсили
    }

    private static Settings jsonToObject(String jsonText) {
        return new Gson().fromJson(jsonText, Settings.class);
    }
}
