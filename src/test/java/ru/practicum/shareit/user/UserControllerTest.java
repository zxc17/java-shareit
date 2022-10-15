package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void add() throws Exception {
        // Assign
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();
        when(userService.add(any())).thenReturn(userDto);

        // Act
        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getById() throws Exception {
        // Assign
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@ya.ru")
                .build();
        when(userService.getById(1L)).thenReturn(userDto);

        // Act
        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getAll() throws Exception {
        // Assign
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@ya.ru")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user2@ya.ru")
                .build();
        when(userService.getAll()).thenReturn(List.of(userDto1, userDto2));

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        JsonArray j = JsonParser.parseString(mvcResult.getResponse().getContentAsString()).getAsJsonArray();
        assertEquals(j.size(), 2);
        assertEquals(j.get(0).getAsJsonObject().get("id").getAsLong(), userDto1.getId());
        assertEquals(j.get(0).getAsJsonObject().get("name").getAsString(), userDto1.getName());
        assertEquals(j.get(0).getAsJsonObject().get("email").getAsString(), userDto1.getEmail());
        assertEquals(j.get(1).getAsJsonObject().get("id").getAsLong(), userDto2.getId());
        assertEquals(j.get(1).getAsJsonObject().get("name").getAsString(), userDto2.getName());
        assertEquals(j.get(1).getAsJsonObject().get("email").getAsString(), userDto2.getEmail());
    }

    @Test
    void update() throws Exception {
        // Assign
        UserDto userDtoUpdated = UserDto.builder()
                .id(1L)
                .name("userUpdated")
                .email("mailUpdated@ya.ru")
                .build();
        when(userService.update(eq(1L), any())).thenReturn(userDtoUpdated);

        // Act
        mockMvc.perform(patch("/users/1")
                        .content(new ObjectMapper().writeValueAsString(userDtoUpdated))
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoUpdated.getId()))
                .andExpect(jsonPath("$.name").value(userDtoUpdated.getName()))
                .andExpect(jsonPath("$.email").value(userDtoUpdated.getEmail()));
    }

    @Test
    void remove() throws Exception {
        // Assign
        doNothing().when(userService).remove(eq(1L));

        // Act
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk());
    }
}