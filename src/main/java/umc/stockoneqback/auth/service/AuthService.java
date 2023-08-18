package umc.stockoneqback.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.auth.service.dto.response.LoginResponse;
import umc.stockoneqback.auth.utils.JwtTokenProvider;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserFindService userFindService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @Transactional
    public LoginResponse login(String loginId, String password) {
        User user = userFindService.findByLoginId(loginId);
        validatePassword(password, user.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        tokenService.synchronizeRefreshToken(user.getId(), refreshToken);

        return new LoginResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                accessToken,
                refreshToken
        );
    }

    private void validatePassword(String comparePassword, Password saved) {
        if(!saved.isSamePassword(comparePassword, ENCODER)) {
            throw BaseException.type(AuthErrorCode.WRONG_PASSWORD);
        }
    }

    @Transactional
    public void logout(Long userId) {
        tokenService.deleteRefreshTokenByMemberId(userId);
        tokenService.deleteFcmToken(userId);
    }

    @Transactional
    public void saveFcm(Long userId, String token) {
        tokenService.saveFcmToken(userId, token);
    }
}
