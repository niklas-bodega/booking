package com.lasias.hostelbookingbackend.repositories;

import com.lasias.hostelbookingbackend.enums.BookingStatus;
import com.lasias.hostelbookingbackend.models.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

//TODO change to UserId

//    List<BookingEntity> findByUser(AppUser user);

    Optional<BookingEntity> findByBookingNumber(String bookingNumber);

    boolean existsByUser_IdAndStatusInAndCheckOutDateAfter(
            Long userId,
            List<BookingStatus> statuses,
            LocalDateTime now
    );
}
