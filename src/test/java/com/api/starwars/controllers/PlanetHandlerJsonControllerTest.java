//package com.api.starwars.controllers;
//
//import com.api.starwars.domain.Planet;
//import com.api.starwars.domain.dtos.PlanetDTO;
//import com.api.starwars.planet.util.EndpointConstants;
//import com.api.starwars.services.IPlanetService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.api.starwars.planet.util.EndpointConstants.API;
//import static com.api.starwars.planet.util.EndpointConstants.PAGINATED;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class PlanetHandlerJsonControllerTest {
//
//    private final static String RIGHT_PLANET_NAME = "Tatooine";
//    private final static String WRONG_PLANET_NAME = "Tatooine Do Meio Fio";
//    private final static Planet PLANET = new Planet(RIGHT_PLANET_NAME, "Semiarido", "Arenoso");
//    private final static Planet WRONG_PLANET = new Planet(WRONG_PLANET_NAME, "Arido", "Molhado");
//    private final static String API_ADDRESS = API + EndpointConstants.PLANET;
//
//    private final static Sort SORT = Sort.by("name").ascending();
//    private final static Integer RIGHT_PAGE = 0;
//    private final static Integer WRONG_PAGE = 12;
//    private final static Integer LENGHT = 15;
//    private final static String ORDER = "name";
//    private final static String DIRECTION = "ASC";
//
//    private final static String RIGHT_ID = "12j312j34h4h4h";
//    private final static String WRONG_ID = "1e4jni4n1";
//    private final static Map<String, Object> map = new HashMap<>();
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private IPlanetService planetService;
//
//    @Test
//    public void should_return_all_companies() throws Exception {
//        when(planetService.getAll()).thenReturn(Collections.singletonList(new PlanetDTO(PLANET, 1)));
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty())
//                .andExpect(jsonPath("$.resultado").isNotEmpty());
//    }
//
//    @Test
//    public void should_return_all_companies_empty() throws Exception {
//        when(planetService.getAll()).thenReturn(Collections.emptyList());
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty())
//                .andExpect(jsonPath("$.resultado").isEmpty());
//    }
//
//    @Test
//    public void should_return_a_not_empty_page() throws Exception {
//        // TODO refactor pageimpl raw use
//        when(planetService.getAllPaginated(RIGHT_PAGE, ORDER, DIRECTION))
//                .thenReturn(new PageImpl(Collections.singletonList(new PlanetDTO(PLANET, 1))));
//
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS + PAGINATED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty())
//                .andExpect(jsonPath("$.resultado.content").isNotEmpty());
//    }
//
//    @Test
//    public void should_return_an_empty_page() throws Exception {
//        when(planetService.getAllPaginated(RIGHT_PAGE, ORDER, DIRECTION))
//                .thenReturn(new PageImpl(Collections.emptyList()));
//
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS + PAGINATED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty())
//                .andExpect(jsonPath("$.resultado.content").isEmpty());
//    }
//
//    @Test
//    public void should_return_a_planet_when_find_by_right_id() throws Exception {
//        PLANET.setId(RIGHT_ID);
//        map.put("alreadyExists", true);
//        map.put("planet", PLANET);
//        when(planetService.alreadyExists(null, RIGHT_ID)).thenReturn(map);
//
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS + "/id/" + RIGHT_ID))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty())
//                .andExpect(jsonPath("$.resultado.id").value(RIGHT_ID));
//    }
//
//    @Test
//    public void should_return_a_planet_when_find_by_wrong_id() throws Exception {
//        WRONG_PLANET.setId(WRONG_ID);
//        map.put("alreadyExists", false);
//        map.put("planet", null);
//        when(planetService.alreadyExists(null, WRONG_ID)).thenReturn(map);
//
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS + "/id/" + WRONG_ID))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.erros").isNotEmpty());
//    }
//
//    @Test
//    public void should_return_a_planet_when_find_by_right_name() throws Exception {
//        map.put("alreadyExists", true);
//        map.put("planet", PLANET);
//        when(planetService.alreadyExists(RIGHT_PLANET_NAME, null)).thenReturn(map);
//
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS + "/nome/" + RIGHT_PLANET_NAME))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty())
//                .andExpect(jsonPath("$.resultado.nome").value(RIGHT_PLANET_NAME));
//    }
//
//    @Test
//    public void should_return_a_planet_when_find_by_wrong_name() throws Exception {
//        map.put("alreadyExists", false);
//        map.put("planet", null);
//        when(planetService.alreadyExists(WRONG_PLANET_NAME, null)).thenReturn(map);
//
//        mockMvc.perform(MockMvcRequestBuilders.get(API_ADDRESS + "/nome/" + WRONG_PLANET_NAME))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.erros").isNotEmpty());
//    }
//
//    @Test
//    public void should_delete_by_right_id() throws Exception {
//        map.put("alreadyExists", true);
//        map.put("planet", PLANET);
//        when(planetService.alreadyExists(null, RIGHT_ID)).thenReturn(map);
//
//        mockMvc.perform(MockMvcRequestBuilders.delete(API_ADDRESS + "/id/" + RIGHT_ID))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty());
//    }
//
//    @Test
//    public void should_insert_planet_successfully() throws Exception {
//        PLANET.setId(RIGHT_ID);
//        map.put("alreadyExists", false);
//        map.put("planet", null);
//        when(planetService.alreadyExists(RIGHT_PLANET_NAME, null)).thenReturn(map);
//        when(planetService.save(PLANET)).thenReturn(PLANET);
//
//        mockMvc.perform(MockMvcRequestBuilders.post(API_ADDRESS)
//                .content(new ObjectMapper().writeValueAsString(PLANET))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.erros").isEmpty())
//                .andExpect(jsonPath("$.resultado.nome").value(RIGHT_PLANET_NAME));
//    }
//
//    @Test
//    public void should_delete_by_wrong_id() throws Exception {
//        map.put("alreadyExists", false);
//        map.put("planet", null);
//        when(planetService.alreadyExists(null, WRONG_ID)).thenReturn(map);
//
//        mockMvc.perform(MockMvcRequestBuilders.delete(API_ADDRESS + "/id/" + WRONG_ID))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.erros").isNotEmpty());
//    }
//}
