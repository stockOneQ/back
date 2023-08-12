package umc.stockoneqback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.user.service.UserWithdrawService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserWithdrawApiController {
    private final UserWithdrawService userWithdrawService;

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdrawUser(@ExtractPayload Long userId) {
        userWithdrawService.withdrawUser(userId);
        return ResponseEntity.ok().build();
    }
}
