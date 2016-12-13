package net.sergey.diplom.rest;

import net.sergey.diplom.domain.user.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RealTime {

    private final List<SseEmitter> emitters = new ArrayList<>();


    @RequestMapping(value = "/getRealTimeMessage")
    public SseEmitter getRealTimeMessageAction() throws IOException {
        final SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                emitters.remove(emitter);
            }
        });
        return emitter;
    }

    @RequestMapping(path = "/chat", method = RequestMethod.GET)
    public Object sendMessage() {
        User user = new User();
        for (SseEmitter emitter : emitters) {
            try {
                user.setId(12);
                user.setUserName("qeqwe");
                emitter.send(user, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
                e.printStackTrace();
            }
        }
        return user;
    }

}
