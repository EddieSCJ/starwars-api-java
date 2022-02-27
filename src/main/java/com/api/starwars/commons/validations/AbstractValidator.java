package com.api.starwars.commons.validations;

import com.api.starwars.commons.exceptions.http.HttpInternalServerErrorException;
import com.api.starwars.commons.helpers.MessageSourceHelper;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

public abstract class AbstractValidator {

    protected final List<String> validationMessages = new ArrayList<>();

    public List<String> validate(Object generic) {
        Field[] declaredFields = generic.getClass().getDeclaredFields();

        try {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                callGenericValidators(field, generic);
            }
        } catch (IllegalAccessException exception) {
            throw new HttpInternalServerErrorException("Ocorre um erro ao fazer validacao dos campos.");
        }

        return validationMessages;
    }

    protected void callGenericValidators(Field field, Object generic) throws IllegalAccessException {
        Object value = field.get(generic);

        isBlank(field, value);
        isNull(field, value);
        isEmptyList(field, value);
        isEmptyArray(field,value);
    }

    protected void addFieldErrorMessage(String field, String message) {
        validationMessages.add(format("Campo {0} {1}", field, message));
    }

    private void isBlank(Field field, Object value) {
        if (field.getType().equals(String.class) && StringUtils.isEmpty((String) value)) {
            String errorMessage = MessageSourceHelper.getFieldErrorMessage("blank");
            addFieldErrorMessage(field.getName(), errorMessage);
        }
    }

    private void isNull(Field field, Object value) {
        if (value == null) {
            String errorMessage = MessageSourceHelper.getFieldErrorMessage("null");
            addFieldErrorMessage(field.getName(), errorMessage);
        }
    }

    private void isEmptyList(Field field, Object value) {

        if (field.getType().equals(List.class)) {
            List<?> list =(List<?>) value;
            if (list != null && list.isEmpty()) {
                String errorMessage = MessageSourceHelper.getFieldErrorMessage("empty");
                addFieldErrorMessage(field.getName(), errorMessage);
            }
        }
    }

    private void isEmptyArray(Field field, Object value) {
        if(field.getType().isArray())  {
            Object[] array = (Object[]) value;
            if (array != null && array.length == 0) {
                String errorMessage = MessageSourceHelper.getFieldErrorMessage("empty");
                addFieldErrorMessage(field.getName(), errorMessage);
            }
        }
    }

}
