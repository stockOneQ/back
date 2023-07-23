package umc.stockoneqback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.controller.dto.request.SignUpManagerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpPartTimerRequest;
import umc.stockoneqback.user.controller.dto.request.SignUpSupervisorRequest;
import umc.stockoneqback.user.controller.dto.request.UserInfoRequest;
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

    @PutMapping("/update/{userId}")
    /*
        - 원래 토큰으로 받아야 하지만, 일단 pathvariable로 했습니다.
        - 인증, 인가가 완료되면 고치면 될 것 같아요!
     */
    public ResponseEntity<Void> updateInformation(@PathVariable Long userId, @RequestBody @Valid UserInfoRequest request) {
        userService.updateInformation(userId, request.name(), request.birth(), request.email(), request.loginId(), request.password(), request.phoneNumber());
        return ResponseEntity.ok().build();
    }
}
