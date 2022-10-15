package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndEndBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItem_Owner_Id(Long ownerId, Pageable pageable);

    List<Booking> findByItem_Id(Long itemId);

    @Query(" select b " +
            "from Booking b " +
            "where b.id <> ?1 and " +
            "      b.item.id = ?4 and " +
            "      (b.start between ?2 and ?3 or " +
            "      b.end between ?2 and ?3) ")
        // Ищет пересечения таймслотов для выбранной вещи.
    List<Booking> findBusyTimeSlot(Long bookingId, LocalDateTime start, LocalDateTime end, Long itemId);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(
            Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartAfter(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    /**
     * Поиск броней вещи выбранным пользователем в указанном статусе, завершенных ранее указанного срока.
     *
     * @param itemId   ID вещи.
     * @param bookerId ID пользователя.
     * @param status   Статус брони.
     * @return Список броней, попадающих под условия.
     */
    @Query(" select b from Booking b " +
            "where  b.item.id = ?1 and " +
            "       b.booker.id = ?2 and " +
            "       b.status = ?3 and " +
            "       b.end < ?4 ")
    List<Booking> countingUsages(Long itemId, Long bookerId, BookingStatus status, LocalDateTime localDateTime);
}
