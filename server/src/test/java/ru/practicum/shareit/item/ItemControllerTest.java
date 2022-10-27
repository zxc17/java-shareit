package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemViewDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final String headerId = "X-Sharer-User-Id";

    @Test
    void add() throws Exception {
        // Assign
        User owner = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .ownerId(owner.getId())
                .build();
        when(itemService.add(any(), eq(owner.getId()))).thenReturn(itemDto);

        // Act
        mockMvc.perform(post("/items")
                        .header(headerId, owner.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void update() throws Exception {
        // Assign
        User owner = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .ownerId(owner.getId())
                .build();
        when(itemService.update(any(), eq(itemDto.getId()), eq(itemDto.getOwnerId()))).thenReturn(itemDto);

        // Act
        mockMvc.perform(patch("/items/1")
                        .header(headerId, owner.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void getById() throws Exception {
        // Assign
        ItemViewDto itemViewDto = ItemViewDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();
        when(itemService.getById(eq(itemViewDto.getId()), eq(33L))).thenReturn(itemViewDto);

        // Act
        mockMvc.perform(get("/items/1")
                        .header(headerId, 33L)
                        .content(objectMapper.writeValueAsString(itemViewDto))
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemViewDto.getId()))
                .andExpect(jsonPath("$.name").value(itemViewDto.getName()))
                .andExpect(jsonPath("$.description").value(itemViewDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemViewDto.getAvailable()));
    }

    @Test
    void getListByOwner() throws Exception {
        // Assign
        User owner = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();
        ItemViewDto itemViewDto1 = ItemViewDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();
        ItemViewDto itemViewDto2 = ItemViewDto.builder()
                .id(2L)
                .name("item2")
                .description("description2")
                .available(true)
                .build();
        when(itemService.getListByOwner(eq(owner.getId()), eq(0L), eq(10)))
                .thenReturn(List.of(itemViewDto1, itemViewDto2));

        // Act
        MvcResult r = mockMvc.perform(get("/items")
                        .header(headerId, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        JsonArray j = JsonParser.parseString(r.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(j.size(), 2);
        assertEquals(j.get(0).getAsJsonObject().get("id").getAsLong(), itemViewDto1.getId());
        assertEquals(j.get(0).getAsJsonObject().get("name").getAsString(), itemViewDto1.getName());
        assertEquals(j.get(0).getAsJsonObject().get("description").getAsString(), itemViewDto1.getDescription());
        assertEquals(j.get(0).getAsJsonObject().get("available").getAsBoolean(), itemViewDto1.getAvailable());
        assertEquals(j.get(1).getAsJsonObject().get("id").getAsLong(), itemViewDto2.getId());
        assertEquals(j.get(1).getAsJsonObject().get("name").getAsString(), itemViewDto2.getName());
        assertEquals(j.get(1).getAsJsonObject().get("description").getAsString(), itemViewDto2.getDescription());
        assertEquals(j.get(1).getAsJsonObject().get("available").getAsBoolean(), itemViewDto2.getAvailable());
    }

    @Test
    void findItems() throws Exception {
        // Assign
        User owner = User.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("Cordless drill")
                .description("Cordless drill")
                .available(true)
                .ownerId(owner.getId())
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .id(2L)
                .name("Screwdriver")
                .description("Cordless screwdriver")
                .available(true)
                .ownerId(owner.getId())
                .build();
        when(itemService.findItems(eq("orDle"), eq(0L), eq(10)))
                .thenReturn(List.of(itemDto1, itemDto2));

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/items/search?text=orDle")
                        .header(headerId, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(j.size(), 2);
        assertEquals(j.get(0).getAsJsonObject().get("id").getAsLong(), itemDto1.getId());
        assertEquals(j.get(0).getAsJsonObject().get("name").getAsString(), itemDto1.getName());
        assertEquals(j.get(0).getAsJsonObject().get("description").getAsString(), itemDto1.getDescription());
        assertEquals(j.get(0).getAsJsonObject().get("available").getAsBoolean(), itemDto1.getAvailable());
        assertEquals(j.get(1).getAsJsonObject().get("id").getAsLong(), itemDto2.getId());
        assertEquals(j.get(1).getAsJsonObject().get("name").getAsString(), itemDto2.getName());
        assertEquals(j.get(1).getAsJsonObject().get("description").getAsString(), itemDto2.getDescription());
        assertEquals(j.get(1).getAsJsonObject().get("available").getAsBoolean(), itemDto2.getAvailable());

    }

    @Test
    void addComment() throws Exception {
        // Assign
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .itemId(1L)
                .authorId(1L)
                .authorName("name")
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        when(itemService.addComment(any(), eq(1L), eq(1L))).thenReturn(commentDto);

        // Act
        mockMvc.perform(post("/items/1/comment")
                        .header(headerId, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))

                //Assign
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.itemId").value(commentDto.getItemId()))
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }
}