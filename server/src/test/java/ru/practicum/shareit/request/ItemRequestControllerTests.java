package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutputDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestInputDto itemRequestInputDto;
    private ItemRequestOutputDto itemRequestOutputDto;
    private Long userId = 1L;
    private Long requestId = 1L;

    @BeforeEach
    void setUp() {
        itemRequestInputDto = new ItemRequestInputDto();
        itemRequestInputDto.setDescription("Test Request");

        itemRequestOutputDto = new ItemRequestOutputDto();
        itemRequestOutputDto.setId(requestId);
        itemRequestOutputDto.setDescription(itemRequestInputDto.getDescription());
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.create(any(ItemRequestInputDto.class), anyLong())).thenReturn(itemRequestOutputDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestOutputDto.getDescription())));
    }

    @Test
    void testGetAllDetailedByUser() throws Exception {
        when(itemRequestService.getAllDetailedByUser(anyLong())).thenReturn(List.of(itemRequestOutputDto));

        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequestOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestOutputDto.getDescription())));
    }

    @Test
    void testGetAll() throws Exception {
        when(itemRequestService.getAll()).thenReturn(List.of(itemRequestOutputDto));

        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemRequestOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].description", is(itemRequestOutputDto.getDescription())));
    }

    @Test
    void testGetDetailedById() throws Exception {
        when(itemRequestService.getOneDetailedById(anyLong())).thenReturn(itemRequestOutputDto);

        mockMvc.perform(get("/requests/" + requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemRequestOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestOutputDto.getDescription())));
    }
}
