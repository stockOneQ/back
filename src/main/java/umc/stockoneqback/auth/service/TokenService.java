package umc.stockoneqback.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.auth.domain.FCMToken;
import umc.stockoneqback.auth.domain.FCMTokenRedisRepository;
import umc.stockoneqback.auth.domain.Token;
import umc.stockoneqback.auth.domain.TokenRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final FCMTokenRedisRepository fcmTokenRedisRepository;

    @Transactional
    public void synchronizeRefreshToken(Long userId, String refreshToken) {
        tokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.createToken(userId, refreshToken))
                );
    }

    @Transactional
    public void reissueRefreshTokenByRtrPolicy(Long userId, String newRefreshToken) {
        tokenRepository.reissueRefreshTokenByRtrPolicy(userId, newRefreshToken);
    }

    @Transactional
    public void deleteRefreshTokenByMemberId(Long userId) {
        tokenRepository.deleteByUserId(userId);
    }

    public boolean isRefreshTokenExists(Long userId, String refreshToken) {
        return tokenRepository.existsByUserIdAndRefreshToken(userId, refreshToken);
    }

    @Transactional
    public void saveFcmToken(Long userId, String token) {
        fcmTokenRedisRepository.deleteById(userId);
        fcmTokenRedisRepository.save(FCMToken.createFCMToken(userId, token));
    }

    @Transactional
    public void deleteFcmToken(Long userId) {
        fcmTokenRedisRepository.deleteById(userId);
    }

    @Transactional
    public List<FCMToken> findAllOnlineUsers() {
        return fcmTokenRedisRepository.findAll();
    }
}
