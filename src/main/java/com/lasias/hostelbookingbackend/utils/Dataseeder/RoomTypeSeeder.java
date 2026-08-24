package com.lasias.hostelbookingbackend.utils.Dataseeder;

import com.lasias.hostelbookingbackend.enums.RoomBadge;
import com.lasias.hostelbookingbackend.models.RoomType;
import com.lasias.hostelbookingbackend.repositories.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
@Order(1)
public class RoomTypeSeeder implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roomTypeRepository.count() == 0) {
            roomTypeRepository.saveAll(roomTypesToAdd());
            log.info("Successfully added {} roomTypes.",roomTypesToAdd().size());
        } else {
            log.info("Room Types already exists in database");
        }
    }

    private List<RoomType> roomTypesToAdd() {
        return List.of(
                RoomType.builder()
                        .name("The Bodega Luxury Suite")
                        .type("Lux Suite")
                        .description("Our crown jewel. A sprawling suite with panoramic Mediterranean views, a private terrace, and bespoke handcrafted furnishings.")
                        .price(480.0)
                        .size(85)
                        .capacity(2)
                        .extraBedAvailable(false)
                        .badge(RoomBadge.SUITE)
                        .featured(true)
                        .imageUrl("https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&q=80&w=1200")
                        .build(),
                RoomType.builder()
                        .name("Classic Single")
                        .type("Single Room")
                        .description("A cozy, thoughtfully appointed room for solo travellers. Warm stone walls, soft linens, and everything you need for a restful stay.")
                        .price(95.0)
                        .size(22)
                        .capacity(1)
                        .extraBedAvailable(false)
                        .badge(RoomBadge.STANDARD)
                        .featured(false)
                        .imageUrl("https://images.unsplash.com/photo-1631049552057-403cdb8f0658?auto=format&fit=crop&q=80&w=800")
                        .build(),

                RoomType.builder()
                        .name("Classic Double")
                        .type("Double Room")
                        .description("Perfect for couples. A generous room with a king-size bed, ensuite bathroom, and warm Mediterranean light flooding through large windows.")
                        .price(150.0)
                        .size(35)
                        .capacity(2)
                        .extraBedAvailable(true)
                        .badge(RoomBadge.STANDARD)
                        .featured(false)
                        .imageUrl("https://images.unsplash.com/photo-1616594039964-ae9021a400a0?auto=format&fit=crop&q=80&w=800")
                        .build(),

                RoomType.builder()
                        .name("Family Suite")
                        .type("Family Suite")
                        .description("Spacious and welcoming, with two bedrooms and a shared living area. Designed for families who refuse to compromise on comfort.")
                        .price(280.0)
                        .size(60)
                        .capacity(4)
                        .extraBedAvailable(false)
                        .badge(RoomBadge.PREMIUM)
                        .featured(false)
                        .imageUrl("https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&q=80&w=800")
                        .build()
        );


    }
}