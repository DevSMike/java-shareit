package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_Id(Long bookerId, Pageable page);

    List<Booking> findAllByItem_Owner_Id(Long ownerId);

    @Query("select b from Booking b where b.item.id = :itemId AND b.booker.id = :bookerId AND b.end <= :now")
    List<Booking> findAllByUserIdAndItemIdAndEndDateIsPassed(Long bookerId, Long itemId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id IN :itemsIds")
    List<Booking> findAllByOwnerItems(List<Long> itemsIds, Pageable page);

    @Query("select b from Booking b where b.item.id IN :itemsIds AND b.status = :waiting")
    List<Booking> findAllByOwnerItemsAndWaitingStatus(List<Long> itemsIds, BookingStatus waiting, Pageable page);

    @Query("select b from Booking b where b.item.id IN :itemsIds AND b.status IN :rejected")
    List<Booking> findAllByOwnerItemsAndRejectedStatus(List<Long> itemsIds, List<BookingStatus> rejected, Pageable page);

    @Query("select b from Booking b where b.item.id IN :itemsIds AND b.start < :now AND b.end > :now")
    List<Booking> findAllByOwnerItemsAndCurrentStatus(List<Long> itemsIds, LocalDateTime now, Pageable page);

    @Query("select b from Booking b where b.item.id IN :itemsIds AND b.start > :now")
    List<Booking> findAllByOwnerItemsAndFutureStatus(List<Long> itemsIds, LocalDateTime now, Pageable page);

    @Query("select b from Booking b where b.item.id IN :itemsIds AND b.end < :now")
    List<Booking> findAllByOwnerItemsAndPastStatus(List<Long> itemsIds, LocalDateTime now, Pageable page);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.status = :waiting")
    List<Booking> findAllByBookerIdAndWaitingStatus(Long bookerId, BookingStatus waiting, Pageable page);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.status IN :rejected")
    List<Booking> findAllByBookerIdAndRejectedStatus(Long bookerId, List<BookingStatus> rejected, Pageable page);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.start < :now AND b.end > :now ")
    List<Booking> findAllByBookerIdAndCurrentStatus(Long bookerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.start > :now ")
    List<Booking> findAllByBookerIdAndFutureStatus(Long bookerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findAllByBookerIdAndPastStatus(Long bookerId, LocalDateTime now, Pageable page);

}
