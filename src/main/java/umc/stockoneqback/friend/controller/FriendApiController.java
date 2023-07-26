package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.service.FriendService;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.user.service.UserFindService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendApiController {
    private final FriendService friendService;

    @PostMapping("/request/{receiverId}")
    public ResponseEntity<Void> requestFriend(@ExtractPayload Long senderId, @PathVariable Long receiverId) {
        Long friendId = friendService.requestFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/request/{receiverId}")
    public ResponseEntity<Void> cancelFriend(@ExtractPayload Long senderId, @PathVariable Long receiverId) {
        friendService.cancelFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/accept/{senderId}")
    public ResponseEntity<Void> acceptFriend(@ExtractPayload Long receiverId, @PathVariable Long senderId) {
        Long friendId = friendService.acceptFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reject/{senderId}")
    public ResponseEntity<Void> rejectFriend(@ExtractPayload Long receiverId, @PathVariable Long senderId) {
        friendService.rejectFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Void> deleteFriend(@ExtractPayload Long userId, @PathVariable Long friendUserId) {
        friendService.deleteFriend(userId, friendUserId);
        return ResponseEntity.ok().build();
    }
}
