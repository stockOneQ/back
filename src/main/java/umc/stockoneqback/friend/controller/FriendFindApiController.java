package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.friend.service.FriendFindService;
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

    @GetMapping("/search")
    public BaseResponse<List<SearchUserResponse>> searchFriends(@ExtractPayload Long userId,
                                                                @RequestParam(value = "name", required = false, defaultValue = "") String searchName)
            throws IOException {
        return new BaseResponse<>(friendFindService.searchFriends(userId, searchName));
    }
}
