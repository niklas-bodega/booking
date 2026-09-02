package com.lasias.hostelbookingbackend;

import com.lasias.hostelbookingbackend.enums.RoomBadge;
import com.lasias.hostelbookingbackend.models.RoomEntity;
import com.lasias.hostelbookingbackend.models.RoomType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private RoomRepository roomRepository;

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
    void shouldGetRoomTypeById() throws Exception {

        Long roomTypeId = 1L;

        mockMvc.perform(get("/api/rooms/roomTypes/{id}", roomTypeId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetRoomTypeByIdFromCreatedData() throws Exception {

        RoomType roomType = RoomType.builder()
                .name("Test name")
                .type("test type")
                .description("test desc")
                .price(9000.0)
                .size(9000)
                .capacity(1)
                .extraBedAvailable(false)
                .badge(RoomBadge.SUITE)
                .featured(false)
                .imageUrl("https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&q=80&w=1200")
                .build();

        roomType = roomTypeRepository.save(roomType);

        mockMvc.perform(get("/api/rooms/roomTypes/{id}", roomType.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(roomType.getId()))
                .andExpect(jsonPath("$.name").value("Test name"));
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
