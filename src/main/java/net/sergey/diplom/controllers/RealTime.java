package net.sergey.diplom.controllers;

import net.sergey.diplom.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@CrossOrigin
@RestController
public class RealTime {

    private final EventService eventService;

    @Autowired
    public RealTime(EventService eventService) {
        this.eventService = eventService;
    }


    @RequestMapping(value = "/getRealTimeMessage")
    public SseEmitter getRealTimeMessageAction() throws IOException {
        final SseEmitter emitter = new SseEmitter();
        eventService.addEmitter(emitter);
        emitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                eventService.removeEmitter(emitter);
            }
        });
        return emitter;
    }

}
