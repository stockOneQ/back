package umc.stockoneqback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.controller.dto.request.SignUpManagerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpPartTimerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpSupervisorRequest;
import umc.stockoneqback.user.service.UserService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;
    private final StoreService storeService;

    @PostMapping("/sign-up/manager")
    public ResponseEntity<Void> signUpManager(@RequestBody @Valid SignUpManagerRequest request) {
        Long savedStoreId = storeService.save(request.storeName(), request.storeSector(), request.storeAddress());
        Long savedUserId = userService.saveManager(request.toUser(), savedStoreId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up/part-timer")
    public ResponseEntity<Void> signUpPartTimer(@RequestBody @Valid SignUpPartTimerRequest request) {
        Long savedUserId = userService.savePartTimer(request.toUser(), request.storeName(), request.storeCode());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up/supervisor")
    public ResponseEntity<Void> signUpSupervisor(@RequestBody @Valid SignUpSupervisorRequest request) {
        Long savedUserId = userService.saveSupervisor(request.toUser(), request.companyName(), request.companyCode());

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER', 'SUPERVISOR')")
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdrawUser(@ExtractPayload Long userId) {
        userService.withdrawUser(userId);

        return ResponseEntity.ok().build();
    }
}
