package com.api.starwars.planets.validations;

import com.api.starwars.common.validations.AbstractValidator;
import com.api.starwars.pkg.helpers.MessageSourceHelper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class PlanetValidator extends AbstractValidator {

    @Override
    protected void isNull(Field field, Object value) {
        if (value == null
                && !field.getName().equals("cacheInDays")
                && !field.getName().equals("id")
        ) {
            String errorMessage = MessageSourceHelper.getFieldErrorMessage("null");
            addFieldErrorMessage(field.getName(), errorMessage);
        }
    }

}

