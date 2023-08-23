package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.UpdatePasswordResponse;

import java.time.LocalDate;

import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserUpdateService {
    private final UserFindService userFindService;
    private final UserRepository userRepository;

    @Transactional
    public void updateInformation(Long userId, String name, LocalDate birth, String email, String loginId, String password, String phoneNumber) {
        User user = userFindService.findById(userId);
        if (!user.getLoginId().equals(loginId)) {
            validateDuplicateLoginId(loginId);
        }

        user.updateInformation(Email.from(email), loginId, Password.encrypt(password, ENCODER), name, birth, phoneNumber);
    }

    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginIdAndStatus(loginId, Status.NORMAL)) {
            throw BaseException.type(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    public UpdatePasswordResponse validateUpdatePassword(String name, LocalDate birth, String loginId) {
        User user = userFindService.findByLoginId(loginId);
        if (!(user.getName().equals(name) && user.getBirth().equals(birth))) {
            throw BaseException.type(UserErrorCode.USER_NOT_FOUND);
        } else {
            return new UpdatePasswordResponse(loginId, Boolean.TRUE);
        }
    }

    public void updatePassword(String loginId, String newPassword, Boolean validate) {
        if (validate.equals(Boolean.TRUE)) {
            User user = userFindService.findByLoginId(loginId);
            user.updatePassword(Password.encrypt(newPassword, ENCODER));
        } else {
            throw BaseException.type(UserErrorCode.NOT_ALLOWED_UPDATE_PASSWORD);
        }
    }
}
