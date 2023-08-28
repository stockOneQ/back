package umc.stockoneqback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.user.controller.dto.request.UpdatePasswordRequest;
import umc.stockoneqback.user.controller.dto.request.UserInfoRequest;
import umc.stockoneqback.user.controller.dto.request.ValidateUpdatePasswordRequest;
import umc.stockoneqback.user.service.UserUpdateService;
import umc.stockoneqback.user.service.dto.response.UpdatePasswordResponse;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserUpdateApiController {
    private final UserUpdateService userUpdateService;

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER', 'SUPERVISOR')")
    @PutMapping("/update")
    public ResponseEntity<Void> updateInformation(@ExtractPayload Long userId, @RequestBody @Valid UserInfoRequest request) {
        userUpdateService.updateInformation(userId, request.name(), request.birth(), request.email(), request.loginId(), request.password(), request.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/update/password")
    public ResponseEntity<UpdatePasswordResponse> validateUpdatePassword(@RequestBody @Valid ValidateUpdatePasswordRequest request) {
        UpdatePasswordResponse response = userUpdateService.validateUpdatePassword(request.name(), request.birth(), request.loginId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        userUpdateService.updatePassword(request.loginId(), request.newPassword(), request.validate());
        return ResponseEntity.ok().build();
    }
}
