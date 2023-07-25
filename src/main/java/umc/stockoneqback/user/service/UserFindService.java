package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFindService {
    private final UserRepository userRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }
}
