package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.exception.ValidationForbiddenException;
import ru.practicum.shareit.exception.ValidationNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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
class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private User owner;
    private User requester;
    private User commentator;

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
        commentator = User.builder()
                .name("commentator")
                .email("commentator@ya.ru")
                .build();
        em.persist(commentator);
    }

    @AfterEach
    void tearDown() {
        em.createNativeQuery("truncate table items, users, comments, bookings");
    }

    @Test
    void add_ok() {
        // Assign
        ItemDto itemDto = ItemDto.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .build();

        // Act
        var result = itemService.add(itemDto, owner.getId());

        // Assert
        assertNotNull(result);
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
        assertEquals(result.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void add_failByOwnerNotFound() {
        // Assign
        ItemDto itemDto = ItemDto.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .build();

        // Act
        var e = assertThrows(ValidationNotFoundException.class,
                () -> itemService.add(itemDto, -1L));

        // Assert
        assertEquals(e.getMessage(), "Владелец ID=-1 не найден.");
    }

    @Test
    void getById_ok() {
        // Assign
        Item item = Item.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
        Comment comment = Comment.builder()
                .itemId(item.getId())
                .created(LocalDateTime.now())
                .authorId(commentator.getId())
                .text("comment")
                .build();
        em.persist(comment);

        // Act
        var resultOwner = itemService.getById(item.getId(), owner.getId());

        // Assert
        assertNotNull(resultOwner);
        assertEquals(resultOwner.getId(), item.getId());
        assertEquals(resultOwner.getName(), item.getName());
        assertEquals(resultOwner.getDescription(), item.getDescription());
        assertEquals(resultOwner.getAvailable(), item.getAvailable());
        assertEquals(resultOwner.getComments().get(0), commentMapper.toCommentDto(comment));
        //TODO Можно добавить проверку на запрос от владельца и другого юзера (наличие дат бронирования).
    }

    @Test
    void getById_fail() {
        // Assign

        // Act
        // Несуществующий item
        var e = assertThrows(ValidationNotFoundException.class,
                () -> itemService.getById(0L, owner.getId()));

        // Assert
        assertEquals(e.getMessage(), "Вещь ID=0 не найдена.");
    }

    @Test
    void getListByOwner_ok() {
        // Assign
        Item item = Item.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
        Comment comment = Comment.builder()
                .itemId(item.getId())
                .created(LocalDateTime.now())
                .authorId(commentator.getId())
                .text("comment")
                .build();
        em.persist(comment);

        // Act
        var result = itemService.getListByOwner(owner.getId(), 0L, 10);

        // Assert
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), itemMapper.toItemViewDto(item, true));
    }

    @Test
    void getListByOwner_fail() {
        // Assign

        // Act
        // Несуществующий владелец.
        var e = assertThrows(ValidationNotFoundException.class,
                () -> itemService.getListByOwner(0L, 0L, 10));

        // Assert
        assertEquals(e.getMessage(), "Владелец ID=0 не найден.");

    }

    @Test
    void update_ok() {
        // Assign
        Item item = Item.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
        ItemDto itemDtoUpdate1 = ItemDto.builder()
                .name("name1")
                .build();
        ItemDto itemDtoUpdate2 = ItemDto.builder()
                .description("desc2")
                .build();
        ItemDto itemDtoUpdate3 = ItemDto.builder()
                .name("name3")
                .description("desc3")
                .available(false)
                .build();

        // Act1
        var result = itemService.update(itemDtoUpdate1, item.getId(), owner.getId());

        // Assert1
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), itemDtoUpdate1.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());

        // Act2
        result = itemService.update(itemDtoUpdate2, item.getId(), owner.getId());

        // Assert2
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), itemDtoUpdate1.getName());
        assertEquals(result.getDescription(), itemDtoUpdate2.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());

        // Act3
        result = itemService.update(itemDtoUpdate3, item.getId(), owner.getId());

        // Assert3
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), itemDtoUpdate3.getName());
        assertEquals(result.getDescription(), itemDtoUpdate3.getDescription());
        assertEquals(result.getAvailable(), itemDtoUpdate3.getAvailable());
    }

    @Test
    void update_fail() {
        // Assign
        Item item = Item.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
        ItemDto itemDtoUpdate = ItemDto.builder()
                .name("name1")
                .build();

        // Act
        // Несуществующий владелец.
        var exceptionOwnerNotFound = assertThrows(ValidationNotFoundException.class,
                () -> itemService.update(itemDtoUpdate, item.getId(), 0L));
        // Несуществующий item.
        var exceptionItem = assertThrows(ValidationNotFoundException.class,
                () -> itemService.update(itemDtoUpdate, 0L, owner.getId()));
        // Запрос не от владельца.
        var exceptionNotOwner = assertThrows(ValidationForbiddenException.class,
                () -> itemService.update(itemDtoUpdate, item.getId(), commentator.getId()));

        // Assert
        assertEquals(exceptionOwnerNotFound.getMessage(), "Владелец ID=0 не найден.");
        assertEquals(exceptionItem.getMessage(), "Вещь ID=0 не найдена.");
        assertEquals(exceptionNotOwner.getMessage(), String
                .format("Невозможно обновить. Вещь принадлежит владельцу ID=%s, запрос прислан от ID=%s.",
                        item.getOwner().getId(), commentator.getId()));

    }

    @Test
    void findItems() {
        // Assign
        Item item1 = Item.builder()
                .name("Аккумуляторная дрель")
                .description("Аккумуляторная дрель + аккумулятор")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item1);
        Item item2 = Item.builder()
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item2);
        Item item3 = Item.builder()
                .name("not")
                .description("not")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item3);

        // Act
        var result = itemService.findItems("аккУМУляторная", 0L, 10);

        // Assert
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), itemMapper.toItemDto(item1));
        assertEquals(result.get(1), itemMapper.toItemDto(item2));
    }

    @Test
    void addComment_ok() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Item item = Item.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
        Booking booking = Booking.builder()
                .start(now.minusHours(2))
                .end(now.minusHours(1))
                .booker(commentator)
                .item(item)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(booking);
        Comment comment = Comment.builder()
                .itemId(item.getId())
                .created(LocalDateTime.now())
                .authorId(commentator.getId())
                .text("comment")
                .build();
        CommentDto commentDto = commentMapper.toCommentDto(comment);

        // Act
        itemService.addComment(commentDto, item.getId(), commentator.getId());

        // Assert
        var result = itemService.getById(item.getId(), requester.getId());
        assertNotNull(result);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        var resultComment = result.getComments();
        assertEquals(resultComment.size(), 1);
        commentDto.setId(resultComment.get(0).getId());
        assertEquals(result.getComments().get(0), commentDto);
    }

    @Test
    void addComment_fail() {
        // Assign
        Item item = Item.builder()
                .name("item")
                .description("desc of item")
                .available(true)
                .owner(owner)
                .build();
        em.persist(item);
        Comment comment = Comment.builder()
                .itemId(item.getId())
                .created(LocalDateTime.now())
                .authorId(commentator.getId())
                .text("comment")
                .build();
        CommentDto commentDto = commentMapper.toCommentDto(comment);
        // Act
        var exceptionUserNotFound = assertThrows(ValidationNotFoundException.class,
                () -> itemService.addComment(commentDto, item.getId(), 0L));
        var exceptionItemNotFound = assertThrows(ValidationNotFoundException.class,
                () -> itemService.addComment(commentDto, 0L, commentator.getId()));
        var exceptionNoBooking = assertThrows(ValidationDataException.class,
                () -> itemService.addComment(commentMapper.toCommentDto(comment), item.getId(), commentator.getId()));

        // Assert
        assertEquals(exceptionUserNotFound.getMessage(), "Пользователь ID=0 не найден.");
        assertEquals(exceptionItemNotFound.getMessage(), "Вещь ID=0 не найдена.");
        assertEquals(exceptionNoBooking.getMessage(), String
                .format("Пользователь ID=%s не пользовался вещью ID=%s.", commentator.getId(), item.getId()));
    }
}