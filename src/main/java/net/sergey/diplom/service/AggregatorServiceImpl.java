package net.sergey.diplom.service;

import com.google.gson.Gson;
import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.domain.ParserImplements;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class AggregatorServiceImpl implements AggregatorService {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DAO DAO;

    @Transactional
    @Override
    public void saveSettings(Filter filter) {
        DAO.saveSettings(filter);
    }

    @Transactional
    @Override
    public Settings loadSetting(String name) {
        List<Filter> settingJSONList = DAO.loadSetting(name);
        String settingJSON;
        if (settingJSONList.size() > 0) {
            settingJSON = settingJSONList.get(0).getAtribJson();
            return jsonToObject(settingJSON);
        }
        return new Settings();
    }

    @Transactional
    @Override
    public String showData(String name) {
        List<Filter> settingJSONList = DAO.loadSetting(name);
        List<ParserImplements> parsersImplementation = DAO.loadParsersImplementation();
        if (settingJSONList.size() > 0) {
            String result = "";
            for (ParserImplements parserImplementation : parsersImplementation) {
                Parser parser = getBeanParserByName(parserImplementation.getParserImplements());
                try {
                    result += parser.getData(settingJSONList.get(0).getAtribJson()) + "</br>";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
        return "нет информации по запрсу";//ничего не напарсили
    }

    @Override
    public Parser getBeanParserByName(String parserImplementation) {
        return applicationContext.getBean(parserImplementation, Parser.class);
    }

    private static Settings jsonToObject(String jsonText) {
        return new Gson().fromJson(jsonText, Settings.class);
    }
}
