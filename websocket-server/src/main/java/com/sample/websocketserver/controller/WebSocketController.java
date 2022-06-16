package com.sample.websocketserver.controller;

import com.sample.websocketserver.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;


public class WebSocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Random RANDOM = new Random();

    @Value("${server.port}")
    private String port;

    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/incoming")
    @SendTo("/topic/outgoing")
    public String incoming(Message message){
        LOGGER.info(String.format("received message: %s", message));
        return String.format("Application on port %s responded to your message: \"%s\"", port, message.getMessage());
    }

    @Scheduled(fixedRate = 15000L)
    public void timed() {
        try {
            // simulate randomness in our timed responses to the client
            Thread.sleep(RANDOM.nextInt(10) * 1000);
            LOGGER.info("sending timed message");
            simpMessagingTemplate.convertAndSend(
                    "/topic/outgoing",
                    String.format("Application on port %s pushed a message!", port)
            );
        } catch (InterruptedException exception) {
            LOGGER.error(String.format("Thread sleep interrupted. Nested exception %s", exception.getMessage()));
        }
    }

}
