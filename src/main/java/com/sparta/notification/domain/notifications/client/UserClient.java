package com.sparta.notification.domain.notifications.client;

import com.sparta.notification.domain.notifications.dto.UserInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {

    @GetMapping("/v1/api/internal/users/me")
    UserInfoResponseDto getMyInfo(@RequestHeader("Authorization") String token);
}
