package umc.stockoneqback.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.auth.service.dto.response.TokenResponse;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public TokenResponse login(String loginId, String password) {
        User user = userFindService.findByLoginId(loginId);
        validatePassword(password, user.getPassword());

        String token = jwtTokenProvider.createToken(user.getId());
        return new TokenResponse(token);
    }

    private void validatePassword(String comparePassword, Password saved) {
        if(!saved.isSamePassword(comparePassword, ENCODER)) {
            throw BaseException.type(AuthErrorCode.WRONG_PASSWORD);
        }
    }
}
