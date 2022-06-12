package com.api.starwars.infra.internal.config.application;

import com.api.starwars.infra.helpers.MessageSourceHelper;
import com.api.starwars.planets.domain.client.StarWarsApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactivefeign.webclient.WebReactiveFeign;

import java.nio.charset.StandardCharsets;

@Configuration
public class ApplicationConfig {
    @Value("${clients.starwars.url}")
    private String starWarsUrl;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public StarWarsApiClient starWarsApiClient() {
        return WebReactiveFeign
                .<StarWarsApiClient>builder()
                .target(StarWarsApiClient.class, starWarsUrl);

    }

    @Bean
    public MessageSourceHelper messageSourceHelper() {
        return new MessageSourceHelper(reloadableResourceBundleMessageSource());
    }

    @Bean
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();

        reloadableResourceBundleMessageSource.setBasename("classpath:messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        reloadableResourceBundleMessageSource.setUseCodeAsDefaultMessage(true);

        return reloadableResourceBundleMessageSource;
    }

}
