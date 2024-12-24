package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemInputDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemInputDto itemInputDto;
    private ItemOutputDto itemOutputDto;
    private CommentInputDto commentInputDto;
    private CommentOutputDto commentOutputDto;
    private Long userId = 1L;
    private Long itemId = 1L;

    @BeforeEach
    void setUp() {
        itemInputDto = new ItemInputDto();
        itemInputDto.setName("Test Item");
        itemInputDto.setDescription("Test Description");
        itemInputDto.setAvailable(true);

        itemOutputDto = ItemOutputDto.builder().build();
        itemOutputDto.setId(itemId);
        itemOutputDto.setName(itemInputDto.getName());
        itemOutputDto.setDescription(itemInputDto.getDescription());
        itemOutputDto.setAvailable(itemInputDto.getAvailable());

        commentInputDto = new CommentInputDto();
        commentInputDto.setText("Test Comment");

        commentOutputDto = new CommentOutputDto();
        commentOutputDto.setId(1L);
        commentOutputDto.setText(commentInputDto.getText());
    }

    @Test
    void testGetAllByUserId() throws Exception {
        when(itemService.getAllByUserId(anyLong())).thenReturn(List.of(itemOutputDto));

        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].name", is(itemOutputDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemOutputDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemOutputDto.getAvailable())));
    }

    @Test
    void testGetById() throws Exception {
        when(itemService.getById(anyLong())).thenReturn(itemOutputDto);

        mockMvc.perform(get("/items/" + itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemOutputDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOutputDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemOutputDto.getAvailable())));
    }

    @Test
    void testFindByText() throws Exception {
        when(itemService.findByText(anyString())).thenReturn(List.of(itemOutputDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(itemOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.[0].name", is(itemOutputDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemOutputDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemOutputDto.getAvailable())));
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.create(anyLong(), any(ItemInputDto.class))).thenReturn(itemOutputDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemOutputDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOutputDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemOutputDto.getAvailable())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemInputDto.class))).thenReturn(itemOutputDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .content(objectMapper.writeValueAsString(itemInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(itemOutputDto.getName())))
                .andExpect(jsonPath("$.description", is(itemOutputDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemOutputDto.getAvailable())));
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentInputDto.class))).thenReturn(commentOutputDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .content(objectMapper.writeValueAsString(commentInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentOutputDto.getId().intValue())))
                .andExpect(jsonPath("$.text", is(commentOutputDto.getText())));
    }
}
