package net.sergey.diplom.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RealTime {

    private final List<SseEmitter> emitters = new ArrayList<>();


  /*  @RequestMapping(value = "/getRealTimeMessage")
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

    }*/

    /*
    @RequestMapping(path = "/chat", method = RequestMethod.POST)
    public String sendMessage() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send("message", MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
                e.printStackTrace();
            }
        }
        return "message";
    }
*/

}
