package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.FindStatus;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.exception.ValidationNotFoundException;
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
class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingMapper bookingMapper;
    private final BookingServiceImpl bookingService;
    private User owner;
    private User requester;
    private User booker;
    private User otherUser;
    private Item item;
    private Item otherItem;
    private Item itemNotAvailable;

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
        booker = User.builder()
                .name("booker")
                .email("booker@ya.ru")
                .build();
        em.persist(booker);
        otherUser = User.builder()
                .name("otherUser")
                .email("otherUser@ya.ru")
                .build();
        em.persist(otherUser);
        item = Item.builder()
                .available(true)
                .name("item")
                .description("description")
                .owner(owner)
                .build();
        em.persist(item);
        otherItem = Item.builder()
                .available(true)
                .name("otherItem")
                .description("description")
                .owner(otherUser)
                .build();
        em.persist(otherItem);
        itemNotAvailable = Item.builder()
                .available(false)
                .name("item not available")
                .description("description not available")
                .owner(owner)
                .build();
        em.persist(itemNotAvailable);
    }

    @AfterEach
    void tearDown() {
        em.createNativeQuery("truncate table items, users, bookings, comments");
    }

    @Test
    void add_ok() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        BookingInDto bookingInDto = BookingInDto.builder()
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .itemId(item.getId())
                .bookerId(booker.getId())
                .status(BookingStatus.WAITING)
                .build();

        // Act
        var result = bookingService.add(bookingInDto);

        // Assert
        assertNotNull(result);
        assertEquals(result.getStart(), bookingInDto.getStart());
        assertEquals(result.getEnd(), bookingInDto.getEnd());
        assertEquals(result.getItem().getId(), bookingInDto.getItemId());
        assertEquals(result.getBooker().getId(), bookingInDto.getBookerId());
        assertEquals(result.getStatus(), bookingInDto.getStatus());
    }

    @Test
    void add_failUserNotFound() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        BookingInDto bookingInDto = BookingInDto.builder()
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .itemId(item.getId())
                .bookerId(0L)
                .status(BookingStatus.WAITING)
                .build();

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.add(bookingInDto));

        // Assert
        assertEquals(result.getMessage(), "Арендатор ID=0 не найден.");
    }

    @Test
    void add_failItemNotFound() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        BookingInDto bookingInDto = BookingInDto.builder()
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .itemId(0L)
                .bookerId(booker.getId())
                .status(BookingStatus.WAITING)
                .build();

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.add(bookingInDto));

        // Assert
        assertEquals(result.getMessage(), "Вещь ID=0 не найдена.");
    }

    @Test
    void add_failItemNotAvailable() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        BookingInDto bookingInDto = BookingInDto.builder()
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .itemId(itemNotAvailable.getId())
                .bookerId(booker.getId())
                .status(BookingStatus.WAITING)
                .build();

        // Act
        var result = assertThrows(ValidationDataException.class,
                () -> bookingService.add(bookingInDto));

        // Assert
        assertEquals(result.getMessage(), String
                .format("Вещь ID=%s недоступна для бронирования.", itemNotAvailable.getId()));
    }

    @Test
    void add_failBookingByOwner() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        BookingInDto bookingInDto = BookingInDto.builder()
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .itemId(item.getId())
                .bookerId(owner.getId())
                .status(BookingStatus.WAITING)
                .build();

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.add(bookingInDto));

        // Assert
        assertEquals(result.getMessage(), "Нельзя арендовать собственную вещь.");
    }

    @Test
    void confirm_ok() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);
        Booking booking2 = Booking.builder()
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking2);

        // Act
        var resultApproved = bookingService.confirm(booking1.getId(), true, owner.getId());
        var resultRejected = bookingService.confirm(booking2.getId(), false, owner.getId());

        // Assert
        assertNotNull(resultApproved);
        assertEquals(resultApproved.getStatus(), BookingStatus.APPROVED);
        assertNotNull(resultRejected);
        assertEquals(resultRejected.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void confirm_failBookingNotFound() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.confirm(0L, true, owner.getId()));

        // Assert
        assertEquals(result.getMessage(), "Бронь ID=0 не найдена.");
    }

    @Test
    void confirm_failOwnerNotFound() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.confirm(booking1.getId(), true, 0L));

        // Assert
        assertEquals(result.getMessage(), "Владелец ID=0 не найден.");
    }

    @Test
    void confirm_failNotOwner() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.confirm(booking1.getId(), true, booker.getId()));

        // Assert
        assertEquals(result.getMessage(), String
                .format("Невозможно обновить. Вещь принадлежит владельцу ID=%s, запрос прислан от ID=%s.",
                        owner.getId(), booker.getId()));
    }

    @Test
    void confirm_failWrongStatus() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(booking1);

        // Act
        var result = assertThrows(ValidationDataException.class,
                () -> bookingService.confirm(booking1.getId(), true, owner.getId()));

        // Assert
        assertEquals(result.getMessage(), String.format("Бронь ID=%s не в статусе WAITING.", booking1.getId()));
    }

    @Test
    void confirm_failTimeSlotBusy() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(booking1);
        Booking booking2 = Booking.builder()
                .start(now.plusDays(2))
                .end(now.plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking2);

        // Act
        var result = assertThrows(ValidationDataException.class,
                () -> bookingService.confirm(booking2.getId(), true, owner.getId()));

        // Assert
        assertEquals(result.getMessage(), "Выбранное время бронирования уже занято.");
    }

    @Test
    void find_ok() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);

        // Act
        var result = bookingService.find(booking1.getId(), booker.getId());

        // Assert
        assertNotNull(result);
        assertEquals(result, bookingMapper.toBookingDto(booking1));
    }

    @Test
    void find_failUserNotFound() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.find(booking1.getId(), 0L));

        // Assert
        assertEquals(result.getMessage(), "Пользователь ID=0 не найден.");
    }

    @Test
    void find_failBookingNotFound() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.find(0L, booker.getId()));

        // Assert
        assertEquals(result.getMessage(), "Бронь ID=0 не найдена.");
    }

    @Test
    void find_failForbidden() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking1);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.find(booking1.getId(), requester.getId()));

        // Assert
        assertEquals(result.getMessage(), String
                .format("Пользователь ID=%s не является ни арендатором, ни владельцем.", requester.getId()));
    }

    @Test
    void findByUser_ok() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking bookingCurrent = Booking.builder()
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingCurrent);
        Booking bookingPast = Booking.builder()
                .start(now.minusDays(3))
                .end(now.minusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingPast);
        Booking bookingFuture = Booking.builder()
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(bookingFuture);
        Booking bookingRejected = Booking.builder()
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
        em.persist(bookingRejected);
        Booking bookingByOther = Booking.builder()
                .start(now.plusDays(5))
                .end(now.plusDays(6))
                .item(otherItem)
                .booker(otherUser)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(bookingByOther);

        // Act
        var resultCurrent = bookingService.findByUser(FindStatus.CURRENT, booker.getId(), 0L, 10);
        var resultPast = bookingService.findByUser(FindStatus.PAST, booker.getId(), 0L, 10);
        var resultFuture = bookingService.findByUser(FindStatus.FUTURE, booker.getId(), 0L, 10);
        var resultWaiting = bookingService.findByUser(FindStatus.WAITING, booker.getId(), 0L, 10);
        var resultRejected = bookingService.findByUser(FindStatus.REJECTED, booker.getId(), 0L, 10);
        var resultAll = bookingService.findByUser(FindStatus.ALL, booker.getId(), 0L, 10);

        // Assert
        assertNotNull(resultCurrent);
        assertEquals(resultCurrent.size(), 2);
        assertEquals(resultCurrent.get(0), bookingMapper.toBookingDto(bookingCurrent));
        assertEquals(resultCurrent.get(1), bookingMapper.toBookingDto(bookingRejected));

        assertNotNull(resultPast);
        assertEquals(resultPast.size(), 1);
        assertEquals(resultPast.get(0), bookingMapper.toBookingDto(bookingPast));

        assertNotNull(resultFuture);
        assertEquals(resultFuture.size(), 1);
        assertEquals(resultFuture.get(0), bookingMapper.toBookingDto(bookingFuture));

        assertNotNull(resultWaiting);
        assertEquals(resultWaiting.size(), 1);
        assertEquals(resultWaiting.get(0), bookingMapper.toBookingDto(bookingFuture));//bookingFuture в статусе Waiting

        assertNotNull(resultRejected);
        assertEquals(resultRejected.size(), 1);
        assertEquals(resultRejected.get(0), bookingMapper.toBookingDto(bookingRejected));

        assertNotNull(resultAll);
        assertEquals(resultAll.size(), 4);
    }

    @Test
    void findByUser_fail() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.findByUser(FindStatus.ALL, 0L, 0L, 10));

        // Assert
        assertEquals(result.getMessage(), "Пользователь ID=0 не найден.");
    }

    @Test
    void findItemsForUser_ok() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking bookingCurrent = Booking.builder()
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingCurrent);
        Booking bookingPast = Booking.builder()
                .start(now.minusDays(3))
                .end(now.minusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(bookingPast);
        Booking bookingFuture = Booking.builder()
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(bookingFuture);
        Booking bookingRejected = Booking.builder()
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
        em.persist(bookingRejected);
        Booking bookingByOther = Booking.builder()
                .start(now.plusDays(5))
                .end(now.plusDays(6))
                .item(otherItem)
                .booker(otherUser)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(bookingByOther);

        // Act
        var resultCurrent = bookingService.findItemsForUser(
                FindStatus.CURRENT, owner.getId(), 0L, 10);
        var resultPast = bookingService.findItemsForUser(
                FindStatus.PAST, owner.getId(), 0L, 10);
        var resultFuture = bookingService.findItemsForUser(
                FindStatus.FUTURE, owner.getId(), 0L, 10);
        var resultWaiting = bookingService.findItemsForUser(
                FindStatus.WAITING, owner.getId(), 0L, 10);
        var resultRejected = bookingService.findItemsForUser(
                FindStatus.REJECTED, owner.getId(), 0L, 10);
        var resultAll = bookingService.findItemsForUser(
                FindStatus.ALL, owner.getId(), 0L, 10);

        // Assert
        assertNotNull(resultCurrent);
        assertEquals(resultCurrent.size(), 2);
        assertEquals(resultCurrent.get(0), bookingMapper.toBookingDto(bookingCurrent));
        assertEquals(resultCurrent.get(1), bookingMapper.toBookingDto(bookingRejected));

        assertNotNull(resultPast);
        assertEquals(resultPast.size(), 1);
        assertEquals(resultPast.get(0), bookingMapper.toBookingDto(bookingPast));

        assertNotNull(resultFuture);
        assertEquals(resultFuture.size(), 1);
        assertEquals(resultFuture.get(0), bookingMapper.toBookingDto(bookingFuture));

        assertNotNull(resultWaiting);
        assertEquals(resultWaiting.size(), 1);
        assertEquals(resultWaiting.get(0), bookingMapper.toBookingDto(bookingFuture)); //bookingFuture в статусе Waiting

        assertNotNull(resultRejected);
        assertEquals(resultRejected.size(), 1);
        assertEquals(resultRejected.get(0), bookingMapper.toBookingDto(bookingRejected));

        assertNotNull(resultAll);
        assertEquals(resultAll.size(), 4);
    }

    @Test
    void findItemsForUser_fail() {
        // Assign
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        em.persist(booking);

        // Act
        var result = assertThrows(ValidationNotFoundException.class,
                () -> bookingService.findItemsForUser(FindStatus.ALL, 0L, 0L, 10));

        // Assert
        assertEquals(result.getMessage(), "Пользователь ID=0 не найден.");
    }
}