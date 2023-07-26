package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
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
                                                                @RequestParam(value = "name", required = false, defaultValue = "") String searchName,
                                                                Pageable pageable) throws IOException {
        return new BaseResponse<>(friendFindService.searchFriends(userId, searchName, pageable));
    }
}
