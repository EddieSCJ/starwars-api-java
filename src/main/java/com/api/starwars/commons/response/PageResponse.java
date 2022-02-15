package com.api.starwars.commons.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class PageResponse<T> {
    Integer page;
    Integer size;
    Integer totalPages;
    Long totalElements;
    T result;

    public static PageResponse fromPage(Page page) {
        return PageResponse
                .builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .result(page.getContent())
                .build();
    }
}
