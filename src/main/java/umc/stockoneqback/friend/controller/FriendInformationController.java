package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.friend.service.FriendInformationService;
import umc.stockoneqback.friend.service.dto.FriendAssembler;
import umc.stockoneqback.global.annotation.ExtractPayload;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendInformationController {
    private final FriendInformationService friendInformationService;

    @GetMapping("")
    public ResponseEntity<FriendAssembler> getFriends(@ExtractPayload Long userId,
                                                      @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId) {
        FriendAssembler response = friendInformationService.getFriends(userId, lastUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/waiting")
    public ResponseEntity<FriendAssembler> getWaitingFriends(@ExtractPayload Long userId,
                                                           @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId) {
        FriendAssembler response = friendInformationService.getWaitingFriends(userId, lastUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requested")
    public ResponseEntity<FriendAssembler> getRequestedFriends(@ExtractPayload Long userId,
                                                             @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId) {
        FriendAssembler response = friendInformationService.getRequestedFriends(userId, lastUserId);
        return ResponseEntity.ok(response);
    }

}
