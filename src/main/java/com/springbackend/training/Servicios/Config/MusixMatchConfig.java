package com.springbackend.training.Servicios.Config;


import org.jmusixmatch.MusixMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@SuppressWarnings("SpringJavaAutowiringInspection")
public class MusixMatchConfig {

    @Autowired
    private Environment env;

    @Bean
    public MusixMatch musixMatch() {
        return new MusixMatch(env.getProperty("musicxmatch.api.key"));
    }
}
