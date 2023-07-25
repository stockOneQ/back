package umc.stockoneqback.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.auth.service.TokenReissueService;
import umc.stockoneqback.auth.service.dto.response.TokenResponse;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.annotation.ExtractToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final TokenReissueService tokenReissueService;

    @PostMapping
    public ResponseEntity<TokenResponse> reissueTokens(@ExtractPayload Long userId, @ExtractToken String refreshToken) {
        TokenResponse tokenResponse = tokenReissueService.reissueTokens(userId, refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}
