package com.lasias.hostelbookingbackend.controllers;

import com.lasias.hostelbookingbackend.dtos.BookingResponseDTO;
import com.lasias.hostelbookingbackend.dtos.CreateBookingRequestDTO;
import com.lasias.hostelbookingbackend.dtos.UpdateBookingRequestDTO;
import com.lasias.hostelbookingbackend.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


//TODO Change AppUser to UserId or jwt token

//    @PostMapping
//    public ResponseEntity<BookingResponseDTO> createBooking(
//            @RequestBody CreateBookingRequestDTO request,
//            AuthenticationPrincipal AppUser user
//    ) {
//        BookingResponseDTO response = bookingService.createBooking(request, user);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

//TODO Change AppUser to UserId or jwt token

//    @GetMapping("/my")
//    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(@AuthenticationPrincipal AppUser user) {
//        List<BookingResponseDTO> response = bookingService.getBookingsByUser(user);
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/{bookingNumber}")
    public ResponseEntity<BookingResponseDTO> getBookingByBookingNumber(@PathVariable String bookingNumber) {
        BookingResponseDTO response = bookingService.getBookingByBookingNumber(bookingNumber);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{bookingNumber}")
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @PathVariable String bookingNumber,
            @RequestBody UpdateBookingRequestDTO request
    ) {
        BookingResponseDTO response = bookingService.updateBooking(bookingNumber, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookingNumber}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String bookingNumber) {
        bookingService.deleteBooking(bookingNumber);

        return ResponseEntity.noContent().build();
    }
}
