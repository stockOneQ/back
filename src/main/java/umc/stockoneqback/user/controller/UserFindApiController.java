package umc.stockoneqback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.dto.response.FindManagerResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserFindApiController {
    private final UserFindService userFindService;

    @GetMapping("/search")
    public ResponseEntity<FindManagerResponse> findManager(@ExtractPayload Long userId,
                                                           @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId,
                                                           @RequestParam(value = "search", required = false, defaultValue = "이름") String searchType,
                                                           @RequestParam(value = "word", required = false, defaultValue = "") String searchWord) {
        return ResponseEntity.ok(userFindService.findManagers(userId, lastUserId, searchType, searchWord));
    }
}
