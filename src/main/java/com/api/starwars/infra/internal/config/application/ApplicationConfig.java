package com.api.starwars.infra.internal.config.application;

import com.api.starwars.infra.helpers.MessageSourceHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;

@Configuration
public class ApplicationConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
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
