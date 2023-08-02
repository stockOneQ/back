package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.LoginIdResponse;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserInformationService {
    private final UserFindService userFindService;

    public LoginIdResponse findLoginId(String name, LocalDate birth, Email email) {
        User user = userFindService.findByEmail(email);
        if (!(user.getName().equals(name) && user.getBirth().equals(birth))) {
            throw BaseException.type(UserErrorCode.USER_NOT_FOUND);
        }

        return new LoginIdResponse(user.getLoginId());
    }
}
