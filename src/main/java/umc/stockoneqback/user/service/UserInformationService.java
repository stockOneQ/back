package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.PartTimerRepository;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.LoginIdResponse;
import umc.stockoneqback.user.service.dto.response.UserInformationResponse;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserInformationService {
    private final UserFindService userFindService;
    private final PartTimerRepository partTimerRepository;

    public LoginIdResponse findLoginId(String name, LocalDate birth, Email email) {
        User user = userFindService.findByEmail(email);
        if (!(user.getName().equals(name) && user.getBirth().equals(birth))) {
            throw BaseException.type(UserErrorCode.USER_NOT_FOUND);
        }

        return new LoginIdResponse(user.getLoginId());
    }

    public UserInformationResponse getInformation(Long userId) {
        User user = userFindService.findById(userId);

        if (user.getRole().equals(Role.MANAGER)) {
            return new UserInformationResponse(user.getId(), user.getEmail().getValue(), user.getLoginId(), user.getName(), user.getBirth(), user.getPhoneNumber(), user.getRole().getAuthority(), user.getManagerStore().getName(), user.getManagerStore().getCode(), user.getManagerStore().getAddress(), null, null);
        } else if (user.getRole().equals(Role.PART_TIMER)) {
            PartTimer partTimer = partTimerRepository.findByPartTimer(user).orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
            return new UserInformationResponse(user.getId(), user.getEmail().getValue(), user.getLoginId(), user.getName(), user.getBirth(), user.getPhoneNumber(), user.getRole().getAuthority(), partTimer.getStore().getName(), null, partTimer.getStore().getAddress(), null, null);
        } else {
            return new UserInformationResponse(user.getId(), user.getEmail().getValue(), user.getLoginId(), user.getName(), user.getBirth(), user.getPhoneNumber(), user.getRole().getAuthority(), null, null, null, user.getCompany().getName(), null);
        }
    }
}
