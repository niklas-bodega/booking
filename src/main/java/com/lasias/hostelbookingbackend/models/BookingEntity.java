package com.lasias.hostelbookingbackend.models;
import com.lasias.hostelbookingbackend.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String bookingNumber = UUID.randomUUID().toString();

    private LocalDateTime checkInDate;

    private LocalDateTime checkOutDate;

    private boolean extraBed;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.CONFIRMED;

    //TODO Change to UserId

//    @ManyToOne
//    private AppUser user;

    @ManyToOne
    private RoomEntity room;

    public BookingEntity() {
    }

    //TODO Change to UserId

//    public BookingEntity(
//            AppUser user,
//            RoomEntity room,
//            LocalDateTime checkInDate,
//            LocalDateTime checkOutDate,
//            boolean extraBed
//    ) {
//        this.user = user;
//        this.room = room;
//        this.checkInDate = checkInDate;
//        this.checkOutDate = checkOutDate;
//        this.extraBed = extraBed;
//        this.status = BookingStatus.CONFIRMED;
//    }

    public Long getId() {
        return id;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }

    public boolean isExtraBed() {
        return extraBed;
    }

    public BookingStatus getStatus() {
        return status;
    }

    //TODO Change to UserId

//    public AppUser getUser() {
//        return user;
//    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public void setExtraBed(boolean extraBed) {
        this.extraBed = extraBed;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    //TODO Change to UserId

//    public void setUser(AppUser user) {
//        this.user = user;
//    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }
}
