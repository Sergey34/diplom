package net.sergey.diplom.rest;

import net.sergey.diplom.domain.model.AirfoilEdit;
import net.sergey.diplom.domain.model.UserView;
import net.sergey.diplom.domain.model.messages.Message;
import net.sergey.diplom.service.ServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Future;

@RequestMapping(value = "/rest/write")
@RestController(value = "write")
public class RestWriteController {
    private final ServiceInt service;

    @Autowired
    public RestWriteController(ServiceInt service) {
        this.service = service;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
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
        System.out.println(airfoilEdit);
        return null;/*service.addAirfoil(shortName, name, details, fileAirfoil, files);*/
    }

    @RequestMapping(value = "/updateAirfoil", method = RequestMethod.POST)
    public Message updateAirfoil(@RequestParam("files") List<MultipartFile> files,
                                 @RequestParam("name") String name,
                                 @RequestParam("ShortName") String shortName,
                                 @RequestParam("Details") String details,
                                 @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        return service.updateAirfoil(shortName, name, details, fileAirfoil, files);
    }


    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public Future<Message> init() {
        return service.parse();
    }
}
