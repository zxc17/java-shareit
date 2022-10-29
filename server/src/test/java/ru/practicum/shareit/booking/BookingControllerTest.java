package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.FindStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.HEADER_ID;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@ya.ru")
                .build();
        booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@ya.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .available(true)
                .name("item")
                .description("description")
                .owner(owner)
                .build();
    }

    @Test
    void add() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        BookingInDto bookingInDto = BookingInDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .itemId(item.getId())
                .bookerId(booker.getId())
                .status(BookingStatus.WAITING)
                .build();
        when(bookingService.add(any())).thenReturn(bookingDto);

        // Act
        mockMvc.perform(post("/bookings")
                        .header(HEADER_ID, 3L)
                        .content(objectMapper.writeValueAsString(bookingInDto))
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void confirm() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.confirm(1L, true, 1L)).thenReturn(bookingDto);

        // Act
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(HEADER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void find() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.find(1L, 1L)).thenReturn(bookingDto);

        // Act
        mockMvc.perform(get("/bookings/1")
                        .header(HEADER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().toString()))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().toString()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void findByUser() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.findByUser(FindStatus.ALL, 2L, 0L, 10))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        // Act
        mockMvc.perform(get("/bookings")
                        .header(HEADER_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto1.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(bookingDto1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.id").value(bookingDto1.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(bookingDto1.getBooker().getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDto1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
                .andExpect(jsonPath("$[1].start").value(bookingDto2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(bookingDto2.getEnd().toString()))
                .andExpect(jsonPath("$[1].item.id").value(bookingDto2.getItem().getId()))
                .andExpect(jsonPath("$[1].booker.id").value(bookingDto2.getBooker().getId()))
                .andExpect(jsonPath("$[1].status").value(bookingDto2.getStatus().toString()));
    }

    @Test
    void findItemsForUser() throws Exception {
        // Assign
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        BookingDto bookingDto1 = BookingDto.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        BookingDto bookingDto2 = BookingDto.builder()
                .id(2L)
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingService.findItemsForUser(FindStatus.ALL, 1L, 0L, 10))
                .thenReturn(List.of(bookingDto1, bookingDto2));

        // Act
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))

                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto1.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto1.getStart().toString()))
                .andExpect(jsonPath("$[0].end").value(bookingDto1.getEnd().toString()))
                .andExpect(jsonPath("$[0].item.id").value(bookingDto1.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(bookingDto1.getBooker().getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDto1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
                .andExpect(jsonPath("$[1].start").value(bookingDto2.getStart().toString()))
                .andExpect(jsonPath("$[1].end").value(bookingDto2.getEnd().toString()))
                .andExpect(jsonPath("$[1].item.id").value(bookingDto2.getItem().getId()))
                .andExpect(jsonPath("$[1].booker.id").value(bookingDto2.getBooker().getId()))
                .andExpect(jsonPath("$[1].status").value(bookingDto2.getStatus().toString()));
    }

}
