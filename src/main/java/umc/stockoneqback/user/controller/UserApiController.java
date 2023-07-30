package umc.stockoneqback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.controller.dto.request.SignUpManagerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpPartTimerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpSupervisorRequest;
import umc.stockoneqback.user.controller.dto.request.UserInfoRequest;
import umc.stockoneqback.user.controller.dto.response.FindManagerResponse;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;
    private final UserFindService userFindService;
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

    @PutMapping("/update")
    public ResponseEntity<Void> updateInformation(@ExtractPayload Long userId, @RequestBody @Valid UserInfoRequest request) {
        userService.updateInformation(userId, request.name(), request.birth(), request.email(), request.loginId(), request.password(), request.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/manager")
    public ResponseEntity<FindManagerResponse> findManager(@ExtractPayload Long userId,
                                                           @RequestParam(value = "condition") String searchCondition,
                                                           @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId,
                                                           @RequestParam(value = "name", required = false, defaultValue = "") String searchWord) {
        return ResponseEntity.ok(userFindService.findManager(userId, searchCondition, lastUserId, searchWord));
    }
}
