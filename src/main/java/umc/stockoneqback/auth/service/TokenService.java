package umc.stockoneqback.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.auth.domain.FcmToken;
import umc.stockoneqback.auth.domain.FcmTokenRedisRepository;
import umc.stockoneqback.auth.domain.RefreshToken;
import umc.stockoneqback.auth.domain.RefreshTokenRedisRepository;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.global.exception.BaseException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final FcmTokenRedisRepository fcmTokenRedisRepository;

    @Transactional
    public void synchronizeRefreshToken(Long userId, String refreshToken) {
        refreshTokenRedisRepository.findById(userId)
                .ifPresentOrElse(
                        token -> {
                            token.updateRefreshToken(refreshToken);
                            refreshTokenRedisRepository.save(token);
                        },
                        () -> refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(userId, refreshToken))
                );
    }

    public RefreshToken findRefreshTokenById(Long userId) {
        return refreshTokenRedisRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN));
    }

    @Transactional
    public void reissueRefreshTokenByRtrPolicy(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = findRefreshTokenById(userId);
        refreshToken.updateRefreshToken(newRefreshToken);
        refreshTokenRedisRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshTokenByMemberId(Long userId) {
        refreshTokenRedisRepository.deleteById(userId);
    }

    public boolean isRefreshTokenExists(Long userId, String refreshToken) {
        return refreshTokenRedisRepository.findById(userId)
                .map(token -> token.getRefreshToken().equals(refreshToken))
                .orElse(false);
    }

    @Transactional
    public void saveFcmToken(Long userId, String token) {
        fcmTokenRedisRepository.deleteById(userId);
        fcmTokenRedisRepository.save(FcmToken.createFcmToken(userId, token));
    }

    @Transactional
    public void deleteFcmToken(Long userId) {
        fcmTokenRedisRepository.deleteById(userId);
    }

    @Transactional
    public List<FcmToken> findAllOnlineUsers() {
        return fcmTokenRedisRepository.findAll();
    }
}
