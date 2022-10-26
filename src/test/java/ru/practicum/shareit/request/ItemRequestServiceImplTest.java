package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemRequestServiceImplTest {
    private final EntityManager em;
    private final ItemRequestServiceImpl itemRequestService;
    private User owner;
    private User requester;
    private User otherUser;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("owner")
                .email("owner@ya.ru")
                .build();
        em.persist(owner);
        requester = User.builder()
                .name("requester")
                .email("requester@ya.ru")
                .build();
        em.persist(requester);
        otherUser = User.builder()
                .name("otherUser")
                .email("otherUser@ya.ru")
                .build();
        em.persist(otherUser);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add_ok() {
        //Assign
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .created(now)
                .description("request")
                .requester(requester)
                .build();

        //Act
        var result = itemRequestService.add(requester.getId(), itemRequestDto);

        //Assert
        assertNotNull(result);
        assertEquals(result.getDescription(), itemRequestDto.getDescription());
        assertEquals(result.getRequester().getId(), itemRequestDto.getRequester().getId());
    }

    @Test
    void add_failNotFound() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .created(now)
                .description("request")
                .requester(requester)
                .build();

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> itemRequestService.add(0L, itemRequestDto));

        // Assert
        assertEquals(result.getMessage(), "Пользователь userID=0 не найден.");
    }

    @Test
    void getByRequester_ok() {
        //Assign
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .created(now)
                .description("request")
                .requester(requester)
                .build();
        em.persist(itemRequest1);
        ItemRequest itemRequest2 = ItemRequest.builder()
                .created(now)
                .description("other request")
                .requester(otherUser)
                .build();
        em.persist(itemRequest2);

        //Act
        var result = itemRequestService.getByRequester(requester.getId());

        //Assert
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getRequester().getId(), itemRequest1.getRequester().getId());
        assertEquals(result.get(0).getDescription(), itemRequest1.getDescription());
    }

    @Test
    void getByRequester_fail() {
        // Assign

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> itemRequestService.getByRequester(0L));

        // Assert
        assertEquals(result.getMessage(), "Пользователь userID=0 не найден.");
    }

    @Test
    void getMadeByOther_ok() {
        //Assign
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .created(now)
                .description("request")
                .requester(requester)
                .build();
        em.persist(itemRequest1);
        ItemRequest itemRequest2 = ItemRequest.builder()
                .created(now)
                .description("other request")
                .requester(otherUser)
                .build();
        em.persist(itemRequest2);

        //Act
        var result = itemRequestService.getMadeByOther(requester.getId(), 0L, 10);

        //Assert
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), itemRequest2.getId());
        assertEquals(result.get(0).getDescription(), itemRequest2.getDescription());
    }

    @Test
    void getMadeByOther_fail() {
        // Assign

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> itemRequestService.getMadeByOther(0L, 0L, 10));

        // Assert
        assertEquals(result.getMessage(), "Пользователь userID=0 не найден.");
    }

    @Test
    void getById_ok() {
        //Assign
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .created(now)
                .description("request")
                .requester(requester)
                .build();
        em.persist(itemRequest1);

        //Act
        var result = itemRequestService.getById(requester.getId(), itemRequest1.getId());

        //Assert
        assertNotNull(result);
        assertEquals(result.getId(), itemRequest1.getId());
        assertEquals(result.getDescription(), itemRequest1.getDescription());
    }

    @Test
    void getById_fail() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .created(now)
                .description("request")
                .requester(requester)
                .build();

        // Act
        var exceptionUserNotFound = assertThrows(ValidationNotFoundException.class,
                () -> itemRequestService.getById(0L, itemRequestDto.getId()));
        var exceptionRequestNotFound = assertThrows(ValidationNotFoundException.class,
                () -> itemRequestService.getById(requester.getId(), 0L));

        // Assert
        assertEquals(exceptionUserNotFound.getMessage(), "Пользователь userID=0 не найден.");
        assertEquals(exceptionRequestNotFound.getMessage(), "Запрос requestId=0 не найден.");
    }

}