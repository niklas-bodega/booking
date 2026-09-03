package com.lasias.hostelbookingbackend.services;

import com.lasias.hostelbookingbackend.config.BookingConstants;
import com.lasias.hostelbookingbackend.dtos.*;
import com.lasias.hostelbookingbackend.enums.BookingStatus;
import com.lasias.hostelbookingbackend.exceptions.NoAvailableRoomException;
import com.lasias.hostelbookingbackend.models.BookingEntity;
import com.lasias.hostelbookingbackend.models.RoomEntity;
import com.lasias.hostelbookingbackend.repositories.BookingRepository;
import com.lasias.hostelbookingbackend.repositories.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserServiceClient userServiceClient;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserServiceClient userServiceClient
    ) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userServiceClient = userServiceClient;
    }

    private boolean userExists(CustomPrincipal principal) {
        return userServiceClient.isUserExists(principal.jwtBearerToken());
    }

    //TODO debug
    @Transactional
    public BookingResponseDTO createBooking(CreateBookingRequestDTO request, CustomPrincipal principal) throws UserPrincipalNotFoundException {
        boolean userExists = userExists(principal);

        if (!userExists) {
            throw new UserPrincipalNotFoundException("User not found");
        }
        validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());
        LocalDateTime checkIn = request.getCheckInDate().atTime(BookingConstants.CHECK_IN_TIME);
        LocalDateTime checkOut = request.getCheckOutDate().atTime(BookingConstants.CHECK_OUT_TIME);

        RoomEntity room = roomRepository.findAvailableByRoomTypeId(
                        request.getRoomTypeId(),
                        checkIn,
                        checkOut,
                        request.isExtraBed()
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoAvailableRoomException("No available room found for selected room type and dates"));

        BookingEntity booking = new BookingEntity(
                principal.userID(),
                room,
                checkIn,
                checkOut,
                request.isExtraBed()
        );

        BookingEntity savedBooking = bookingRepository.save(booking);

        return toResponseDTO(savedBooking);
    }

    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public BookingResponseDTO getBookingByBookingNumber(String bookingNumber) {
        BookingEntity booking = findBookingByBookingNumber(bookingNumber);

        return toResponseDTO(booking);
    }

    @Transactional
    public BookingResponseDTO updateBooking(String bookingNumber, UpdateBookingRequestDTO request) {
        validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());

        BookingEntity booking = findBookingByBookingNumber(bookingNumber);
        LocalDateTime checkIn = request.getCheckInDate().atTime(BookingConstants.CHECK_IN_TIME);
        LocalDateTime checkOut = request.getCheckOutDate().atTime(BookingConstants.CHECK_OUT_TIME);

        RoomEntity room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        boolean roomAvailable = roomRepository.isRoomAvailableForBookingUpdate(
                room.getId(),
                booking.getBookingNumber(),
                checkIn,
                checkOut,
                request.isExtraBed()
        );

        if (!roomAvailable) {
            throw new NoAvailableRoomException("Selected room is not available for selected dates");
        }

        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setExtraBed(request.isExtraBed());

        BookingEntity savedBooking = bookingRepository.save(booking);

        return toResponseDTO(savedBooking);
    }

    @Transactional
    public void deleteBooking(String bookingNumber) {
        BookingEntity booking = findBookingByBookingNumber(bookingNumber);

        booking.setStatus(BookingStatus.CANCELLED);

        bookingRepository.save(booking);
    }

    private BookingEntity findBookingByBookingNumber(String bookingNumber) {
        return bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    private void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }

        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    private BookingResponseDTO toResponseDTO(BookingEntity booking) {
        RoomEntity room = booking.getRoom();

        RoomResponseDTO roomResponseDTO = new RoomResponseDTO(
                room.getId(),
                room.getRoomNumber(),
                room.isExtraBed(),
                room.getRoomType()
        );

        return new BookingResponseDTO(
                booking.getBookingNumber(),
                roomResponseDTO,
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.isExtraBed(),
                booking.getStatus().name()
        );
    }


    public boolean hasActiveBookings(Long userId) {
        return bookingRepository.existsByUserIdAndStatusInAndCheckOutDateAfter(
                userId,
                List.of(BookingStatus.CONFIRMED, BookingStatus.AWAITING_CONFIRMATION),
                LocalDateTime.now());
    }
}
