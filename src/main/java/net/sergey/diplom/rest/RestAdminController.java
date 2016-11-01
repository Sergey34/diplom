package net.sergey.diplom.rest;


import net.sergey.diplom.service.ServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/rest/admin")
@RestController
public class RestAdminController {

    @Autowired
    ServiceInt service;

    @RequestMapping(value = "/initDB", method = RequestMethod.GET)
    public void initDB() {

    }

    @RequestMapping(value = "/cleanInitDB", method = RequestMethod.GET)
    public void cleanInitDB() {
        service.clean();
    }
}
