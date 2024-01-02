package com.springbackend.training.Config;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.springframework.core.env.Environment;
import java.io.IOException;


import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackErrorConfig {

    private final MethodsClient methodsClient;
    public SlackErrorConfig(Environment environment) {
        this.methodsClient = Slack.getInstance().methods(environment.getProperty("slack.access.token"));
    }

    public void sendErrorMessage (Exception e) {
        ChatPostMessageRequest messageRequest = ChatPostMessageRequest.builder()
                .channel("#exceptions-chat")
                .username("Tu querido amigo el backend")
                .text("Error: "+ e.getMessage() + "\n" + "Clase: " +  e.getClass() + "\n" + "Donde?: No se buscalo master" )
                .build();
        try {
            ChatPostMessageResponse response = methodsClient.chatPostMessage(messageRequest);
            if (!response.isOk()) {
                throw new RuntimeException("Failed to send message: " + response.getError());
            }
        } catch (IOException | SlackApiException error) {
            throw new RuntimeException("Failed to send message", error);
        }
    }
}
