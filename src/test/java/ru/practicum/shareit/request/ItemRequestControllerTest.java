package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private User requester;

    @BeforeEach
    void setUp() {
        requester = User.builder()
                .id(3L)
                .name("requester")
                .email("requester@ya.ru")
                .build();
    }

    @Test
    void add() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(now)
                .build();
        when(itemRequestService.add(eq(requester.getId()), any())).thenReturn(itemRequestDto);

        // Act
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 3L)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requester.id").value(itemRequestDto.getRequester().getId()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()))
                .andExpect(jsonPath("$.items").value(itemRequestDto.getItems()));
    }

    @Test
    void getByRequester() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(now)
                .build();
        when(itemRequestService.getByRequester(eq(requester.getId()))).thenReturn(List.of(itemRequestDto));

        // Act
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requester.id").value(itemRequestDto.getRequester().getId()))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated().toString()))
                .andExpect(jsonPath("$[0].items").value(itemRequestDto.getItems()));
    }

    @Test
    void getMadeByOther() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(now)
                .build();
        when(itemRequestService.getMadeByOther(eq(requester.getId()), eq(0L), eq(10)))
                .thenReturn(List.of(itemRequestDto));

        // Act
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requester.id").value(itemRequestDto.getRequester().getId()))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated().toString()))
                .andExpect(jsonPath("$[0].items").value(itemRequestDto.getItems()));
    }

    @Test
    void getById() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(requester)
                .created(now)
                .build();
        when(itemRequestService.getById(eq(requester.getId()), eq(itemRequestDto.getId())))
                .thenReturn(itemRequestDto);

        // Act
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 3L)
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requester.id").value(itemRequestDto.getRequester().getId()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().toString()))
                .andExpect(jsonPath("$.items").value(itemRequestDto.getItems()));
    }
}
