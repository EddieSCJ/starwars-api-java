package com.api.starwars.common.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    Integer page;
    Integer size;
    Integer totalPages;
    Long totalElements;
    List<T> result;

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
