package umc.stockoneqback.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.business.service.BusinessListService;
import umc.stockoneqback.business.service.BusinessService;
import umc.stockoneqback.business.service.dto.BusinessListResponse;
import umc.stockoneqback.friend.service.dto.FriendAssembler;
import umc.stockoneqback.global.annotation.ExtractPayload;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business")
public class BusinessListApiController {
    private final BusinessListService businessListService;

    @GetMapping("/supervisors")
    public ResponseEntity<BusinessListResponse> getFriends(@ExtractPayload Long userId,
                                                           @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId) {
        BusinessListResponse response = businessListService.getSupervisor(userId, lastUserId);
        return ResponseEntity.ok(response);
    }
}
