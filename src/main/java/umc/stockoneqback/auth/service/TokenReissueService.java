package umc.stockoneqback.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.auth.service.dto.response.TokenResponse;
import umc.stockoneqback.auth.utils.JwtTokenProvider;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.GlobalErrorCode;

@Service
@RequiredArgsConstructor
public class TokenReissueService {
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse reissueTokens(Long userId, String refreshToken) {
        // 사용자가 보유하고 있는 Refresh Token인지
        if (!tokenService.isRefreshTokenExists(userId, refreshToken)) {
            throw BaseException.type(GlobalErrorCode.INVALID_TOKEN);
        }

        // Access Token & Refresh Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        // RTR 정책에 의해 memberId에 해당하는 사용자가 보유하고 있는 Refresh Token 업데이트
        tokenService.reissueRefreshTokenByRtrPolicy(userId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
