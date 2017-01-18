package net.sergey.diplom.rest;

import net.sergey.diplom.dto.UserView;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.service.ServiceInt;
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
    private final ServiceInt service;

    @Autowired
    public RestWriteController(ServiceInt service) {
        this.service = service;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Message addUser(@RequestBody UserView userView) {
        return service.addUser(userView);
    }

    @RequestMapping(value = "/addAirfoilForFileCsv", method = RequestMethod.POST)
    public Message addAirfoilForFileCsv(@RequestParam("files") List<MultipartFile> files,
                                        @RequestParam("name") String name,
                                        @RequestParam("ShortName") String shortName,
                                        @RequestParam("Details") String details,
                                        @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        return service.addAirfoil(shortName, name, details, fileAirfoil, files);
    }

    @RequestMapping(value = "/addAirfoilForStringCsv", method = RequestMethod.POST)
    public Message addAirfoilForStringCsv(@RequestBody AirfoilEdit airfoilEdit) {
        return service.addAirfoil(airfoilEdit);
    }

    @RequestMapping(value = "/updateAirfoil", method = RequestMethod.POST)
    public Message updateAirfoil(@RequestParam("files") List<MultipartFile> files,
                                 @RequestParam("name") String name,
                                 @RequestParam("ShortName") String shortName,
                                 @RequestParam("Details") String details,
                                 @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        return service.updateAirfoil(shortName, name, details, fileAirfoil, files);
    }

    @RequestMapping(value = "/updateAirfoilStringCsv", method = RequestMethod.POST)
    public Message updateAirfoilStringCsv(@RequestBody AirfoilEdit airfoilEdit) {
        return service.updateAirfoil(airfoilEdit);
    }


    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public Future<Message> init() {
        if (service.parsingIsStarting()) {
            return new AsyncResult<>(new Message("В данный момент данные уже кем-то обновляются. Необходимо дождаться завершения обновления", SC_FORBIDDEN));
        }
        return service.parse();
    }

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public Message stop() {
        return service.stop();
    }
}
