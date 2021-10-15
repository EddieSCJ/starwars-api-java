package com.api.commons.response;

public enum HttpResponses {
    OK(200, "OK"),
    ALREADY_EXISTS(406, "O planeta indicado já consta na nossa base de dados, portanto não podemos adicioná-lo"),
    BAD_REQUEST(400, "Você passou parâmetros inválidos ou está faltando algo"),
    UNABLE_PERFORM_DELETE(500, "Não conseguimos processar o pedido de exclusão, por favor, contate o time"),
    NOT_FOUND(404, "Não encontramos nenhum resultado");

    private final Integer code;
    private final String description;

    HttpResponses(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
}
