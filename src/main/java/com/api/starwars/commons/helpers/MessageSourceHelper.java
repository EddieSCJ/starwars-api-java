package com.api.starwars.commons.helpers;

import org.springframework.context.MessageSource;

import java.util.Locale;

import static java.text.MessageFormat.format;

public class MessageSourceHelper {

    private static MessageSource messageSource;

    public MessageSourceHelper(MessageSource source) {
        messageSource = source;
    }

    public static String getApiErrorMessage(String error) {
        return messageSource.getMessage(format("error.api.{0}", error), null, new Locale("pt-br"));

    }

}
