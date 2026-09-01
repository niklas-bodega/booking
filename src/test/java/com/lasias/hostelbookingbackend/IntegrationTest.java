package com.lasias.hostelbookingbackend;

import com.lasias.hostelbookingbackend.models.RoomEntity;
import com.lasias.hostelbookingbackend.models.RoomType;
import com.lasias.hostelbookingbackend.repositories.BookingRepository;
import com.lasias.hostelbookingbackend.repositories.RoomRepository;
import com.lasias.hostelbookingbackend.repositories.RoomTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class IntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name",
                mysql::getDriverClassName);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testIntegration() {

        assertTrue(true);
    }

    //Room Type Integration Test

    @Test
    void shouldGetAllRoomTypes() throws Exception {

        mockMvc.perform(get("/api/rooms/roomTypes"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetRoomTypesByAvailability() throws Exception {

        mockMvc.perform(get("/api/rooms/roomTypes/available")
                        .param("checkInDate", "2026-10-10")
                        .param("checkOutDate", "2026-10-12")
                        .param("nrOfGuests", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCheckRoomTypeAvailability() throws Exception {

        Long roomTypeId = 1L;

        mockMvc.perform(get("/api/rooms/roomTypes/available/{id}", roomTypeId)
                        .param("checkInDate", "2026-10-10")
                        .param("checkOutDate", "2026-10-12")
                        .param("bookingNumber", "test-booking"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetRoomTypeById() throws Exception {

        Long roomTypeId = 1L;

        mockMvc.perform(get("/api/rooms/roomTypes/{id}", roomTypeId))
                .andExpect(status().isOk());
    }

    // Room Integration Test
    @Test
    void shouldGetRooms() throws Exception {

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetRoom() throws Exception {

        RoomEntity room = new RoomEntity();
        room.setRoomNumber(101L);

        RoomEntity savedRoom = roomRepository.save(room);

        mockMvc.perform(get("/api/rooms/{id}", savedRoom.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRoom() throws Exception {

        RoomEntity room = new RoomEntity();
        room.setRoomNumber(101L);

        RoomEntity savedRoom = roomRepository.save(room);

        mockMvc.perform(delete("/api/rooms/{id}", savedRoom.getId()))
                .andExpect(status().isOk());

        assertFalse(roomRepository.existsById(savedRoom.getId()));
    }
}
