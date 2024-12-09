package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    // текущие (CURRENT) бронирования пользователя
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    // завершенные (PAST) бронирования пользователя
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    // будущие (FUTURE) бронирования пользователя
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    // с определенным статусом (WAITING / REJECTED) бронирования пользователя
    List<Booking> findByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

}
