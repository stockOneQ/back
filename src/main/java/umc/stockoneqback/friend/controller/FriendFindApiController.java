package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.friend.service.FriendFindService;
import umc.stockoneqback.friend.service.FriendInformationService;
import umc.stockoneqback.friend.service.dto.FriendAssembler;
import umc.stockoneqback.friend.service.dto.SearchUserResponse;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendFindApiController {
    private final FriendFindService friendFindService;
    private final FriendInformationService friendInformationService;

    @GetMapping("/search")
    public BaseResponse<List<SearchUserResponse>> searchFriends(@ExtractPayload Long userId,
                                                                @RequestParam(value = "name", required = false, defaultValue = "") String searchName,
                                                                Pageable pageable) throws IOException {
        return new BaseResponse<>(friendFindService.searchFriends(userId, searchName, pageable));
    }

    @GetMapping("")
    public BaseResponse<FriendAssembler> getFriends(@ExtractPayload Long userId,
                                                    @RequestParam(value = "last", required = false) Long lastUserId) {
        FriendAssembler response = friendInformationService.getFriends(userId, lastUserId);
        return new BaseResponse<>(response);
    }

    @GetMapping("/waiting")
    public BaseResponse<FriendAssembler> getWaitingFriends(@ExtractPayload Long userId,
                                                           @RequestParam(value = "last", required = false) Long lastUserId) {
        FriendAssembler response = friendInformationService.getWaitingFriends(userId, lastUserId);
        return new BaseResponse<>(response);
    }

    @GetMapping("/requested")
    public BaseResponse<FriendAssembler> getRequestedFriends(@ExtractPayload Long userId,
                                                             @RequestParam(value = "last", required = false) Long lastUserId) {
        FriendAssembler response = friendInformationService.getRequestedFriends(userId, lastUserId);
        return new BaseResponse<>(response);
    }
}
