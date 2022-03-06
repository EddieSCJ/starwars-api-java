package com.api.starwars.commons;

import com.api.starwars.commons.response.PageResponse;
import commons.utils.DomainUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageResponseTest {

    @Test
    @DisplayName("Deve fazer a conversao a partir de uma pagina")
    public void fromPageSuccessfully() {
        Page page = new PageImpl<>(
                List.of(DomainUtils.getRandomPlanetJson()),
                PageRequest.of(1, 15, Sort.by("name").ascending()),
                1
        );

        PageResponse expectedPlanetJson = PageResponse
                .builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .result(page.getContent())
                .build();

        assertTrue(Objects.deepEquals(expectedPlanetJson,PageResponse.fromPage(page)));
    }

}
