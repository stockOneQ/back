package umc.stockoneqback.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.auth.controller.dto.request.LoginRequest;
import umc.stockoneqback.auth.service.AuthService;
import umc.stockoneqback.auth.service.dto.response.TokenResponse;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = authService.login(request.loginId(), request.password());
        return ResponseEntity.ok(response);
    }
}
