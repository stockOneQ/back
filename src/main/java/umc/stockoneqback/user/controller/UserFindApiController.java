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

    @GetMapping("/search/manager")
    public ResponseEntity<FindManagerResponse> findManager(@ExtractPayload Long userId,
                                                           @RequestParam(value = "condition") String searchCondition,
                                                           @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId,
                                                           @RequestParam(value = "name", required = false, defaultValue = "") String searchWord) {
        return ResponseEntity.ok(userFindService.findManager(userId, searchCondition, lastUserId, searchWord));
    }
}
