package com.atypon.read_controller.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalStorage {
    @Bean
    public String secretKey() {
        return "averylongsecretkey123+";
    }
}
