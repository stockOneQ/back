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
        if (!tokenService.isRefreshTokenExists(userId, refreshToken)) {
            throw BaseException.type(GlobalErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        tokenService.reissueRefreshTokenByRtrPolicy(userId, newRefreshToken);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
