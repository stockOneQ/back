package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.friend.service.FriendService;
import umc.stockoneqback.global.annotation.ExtractPayload;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendApiController {
    private final FriendService friendService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/request/{receiverId}")
    public ResponseEntity<Void> requestFriend(@ExtractPayload Long senderId, @PathVariable Long receiverId) {
        Long friendId = friendService.requestFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/request/{receiverId}")
    public ResponseEntity<Void> cancelFriend(@ExtractPayload Long senderId, @PathVariable Long receiverId) {
        friendService.cancelFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/accept/{senderId}")
    public ResponseEntity<Void> acceptFriend(@ExtractPayload Long receiverId, @PathVariable Long senderId) {
        Long friendId = friendService.acceptFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/reject/{senderId}")
    public ResponseEntity<Void> rejectFriend(@ExtractPayload Long receiverId, @PathVariable Long senderId) {
        friendService.rejectFriend(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Void> deleteFriend(@ExtractPayload Long userId, @PathVariable Long friendUserId) {
        friendService.deleteFriend(userId, friendUserId);
        return ResponseEntity.ok().build();
    }
}
