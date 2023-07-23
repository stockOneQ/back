package umc.stockoneqback.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.auth.controller.dto.request.LoginRequest;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.auth.service.dto.response.LoginResponse;
import umc.stockoneqback.global.annotation.ExtractPayload;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request.loginId(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@ExtractPayload Long userId) {
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
}
