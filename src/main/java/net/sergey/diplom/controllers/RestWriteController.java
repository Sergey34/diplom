package net.sergey.diplom.controllers;

import net.sergey.diplom.dto.UserView;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.services.ServiceAirfoil;
import net.sergey.diplom.services.parser.Parser;
import net.sergey.diplom.services.usermanagerservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Future;

import static net.sergey.diplom.dto.messages.Message.SC_FORBIDDEN;

@RequestMapping(value = "/rest/write")
@RestController(value = "write")
public class RestWriteController {
    private final ServiceAirfoil serviceAirfoil;
    private final UserService userService;
    private final Parser parserService;

    @Autowired
    public RestWriteController(ServiceAirfoil serviceAirfoil, UserService userService, Parser parserService) {
        this.serviceAirfoil = serviceAirfoil;
        this.userService = userService;
        this.parserService = parserService;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Message addUser(@RequestBody UserView userView) {
        return userService.addUser(userView);
    }

    @RequestMapping(value = "/addAirfoilForFileCsv", method = RequestMethod.POST)
    public Message addAirfoilForFileCsv(@RequestParam("files") List<MultipartFile> files,
                                        @RequestParam("name") String name,
                                        @RequestParam("ShortName") String shortName,
                                        @RequestParam("Details") String details,
                                        @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        return serviceAirfoil.addAirfoil(shortName, name, details, fileAirfoil, files);
    }

    @RequestMapping(value = "/addAirfoilForStringCsv", method = RequestMethod.POST)
    public Message addAirfoilForStringCsv(@RequestBody AirfoilEdit airfoilEdit) {
        return serviceAirfoil.addAirfoil(airfoilEdit);
    }

    @RequestMapping(value = "/updateAirfoil", method = RequestMethod.POST)
    public Message updateAirfoil(@RequestParam("files") List<MultipartFile> files,
                                 @RequestParam("name") String name,
                                 @RequestParam("ShortName") String shortName,
                                 @RequestParam("Details") String details,
                                 @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        return serviceAirfoil.updateAirfoil(shortName, name, details, fileAirfoil, files);
    }

    @RequestMapping(value = "/updateAirfoilStringCsv", method = RequestMethod.POST)
    public Message updateAirfoilStringCsv(@RequestBody AirfoilEdit airfoilEdit) {
        return serviceAirfoil.updateAirfoil(airfoilEdit);
    }


    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public Future<Message> init() {
        if (parserService.parsingIsStarting()) {
            return new AsyncResult<>(new Message("В данный момент данные уже кем-то обновляются. Необходимо дождаться завершения обновления", SC_FORBIDDEN));
        }
        return parserService.startParsing();
    }

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public Message stop() {
        return parserService.stopParsing();
    }

    @RequestMapping(value = "/clearAll", method = RequestMethod.GET)
    public Message clearAll() {
        return serviceAirfoil.clearAll();
    }

}
