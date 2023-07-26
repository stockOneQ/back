package umc.stockoneqback.friend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.friend.service.dto.SearchUserResponse;
import umc.stockoneqback.friend.service.FriendFindService;
import umc.stockoneqback.global.base.BaseResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendFindApiController {
    private final FriendFindService friendFindService;

    @GetMapping("/search/friend")
    public BaseResponse<List<SearchUserResponse>> searchFriends(@PathVariable Long userId,
                                                                @RequestParam(value = "name") String searchName,
                                                                Pageable pageable) throws IOException {
        return new BaseResponse<>(friendFindService.searchFriends(userId, searchName, pageable));
    }
}
