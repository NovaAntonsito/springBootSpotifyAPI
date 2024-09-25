package com.springbackend.training.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Objects;

@Configuration
public class SecurityConfigPassword {

    private final Environment env;

    public SecurityConfigPassword(Environment env) {
        this.env = env;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Define the strength and the secret for BCrypt
        return new BCryptPasswordEncoder(10, new SecureRandom(Objects.requireNonNull(env.getProperty("super.ultra.special.key.for.jajas")).getBytes()));
    }

}
