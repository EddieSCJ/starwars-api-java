package com.api.starwars.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response<T> {
    @JsonProperty("erros")
    List<String> errors = new ArrayList<String>();
    @JsonProperty("resultado")
    T result = null;
    Integer status = 200;
}
