package com.lasias.hostelbookingbackend.services;

import com.lasias.hostelbookingbackend.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestClient userRestClient;

    //get on /api/user with jwt will return 401 unauth if user does not exist and return 200 if exists
    public boolean isUserExists(String jwt) {
        return userRestClient.get()
                .uri("/api/user")
                .header("Authorization", jwt)
                .retrieve()
                .toBodilessEntity()
                .getStatusCode()
                .value() == 200;
    }
}