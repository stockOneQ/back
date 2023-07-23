package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.service.FriendService;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendApiController {
    private final FriendService friendService;
    private final UserService userService;

    @GetMapping("/search/friend")
    public ResponseEntity<List<Friend>> searchFriends(@AuthenticationPrincipal User user) {
        List<Friend> friends = friendService.searchFriends(user);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/request/{friendId}")
    public ResponseEntity<Friend> sendFriendRequest(@AuthenticationPrincipal User requester, @PathVariable Long friendId) {
        User friend = userService.findById(friendId);
        Friend newRequest = friendService.sendFriendRequest(requester, friend);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    @PatchMapping("/{friendId}")
    public ResponseEntity<Friend> updateFriendRequest(@PathVariable Long friendId, @RequestParam("status") FriendStatus status) {
        Friend updatedRequest = friendService.updateFriendRequest(friendId, status);
        return ResponseEntity.ok(updatedRequest);
    }
}
