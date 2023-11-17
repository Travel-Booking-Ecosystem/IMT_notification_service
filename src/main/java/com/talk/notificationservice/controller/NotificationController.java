package com.talk.notificationservice.controller;



import com.talk.notificationservice.dto.response.CommonResponse;
import com.talk.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;
    @GetMapping("/health")
    public ResponseEntity<CommonResponse> health() {
        Map<String, String> map = Map.of(
                "service", "notification-service",
                "status", "OK",
                "time", LocalDateTime.now().toString());
        return ResponseEntity.ok(CommonResponse.success("Health check",map));
    }


    @GetMapping("/all")
    public ResponseEntity<CommonResponse> getNotifications(@RequestHeader String currentUserId) {
        return notificationService.getNotifications(currentUserId);
    }

    @PostMapping("/see-all-notifications")
    public ResponseEntity<CommonResponse> seeAllNotifications(@RequestHeader String currentUserId) {
        return notificationService.seeAllNotifications(currentUserId);
    }


}
