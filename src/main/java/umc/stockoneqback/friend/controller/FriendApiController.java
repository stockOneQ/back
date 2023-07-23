package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.dto.SearchUserResponse;
import umc.stockoneqback.friend.service.FriendService;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendApiController {
    private final FriendService friendService;
    private final UserFindService userService;

    @GetMapping("/search/friend")
    public BaseResponse<List<SearchUserResponse>> searchFriends(@PathVariable Long userId,
                                                                @RequestParam(value = "name") String searchName) throws IOException {
        return new BaseResponse<>(friendService.searchFriends(userId, searchName));
    }

    @PostMapping("/request/{friendId}")
    public ResponseEntity<Friend> sendFriendRequest(@ExtractPayload Long reqUserId, @PathVariable Long friendId) {
        User friend = userService.findById(friendId);
        Friend newRequest = friendService.sendFriendRequest(reqUserId, friend);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    @PatchMapping("/{friendId}")
    public ResponseEntity<Friend> updateFriendRequest(@ExtractPayload Long reqUserId, @PathVariable Long friendId,
                                                      @RequestParam("status") FriendStatus status) {
        Friend updatedRequest = friendService.updateFriendRequest(friendId, status);
        return ResponseEntity.ok(updatedRequest);
    }
}
