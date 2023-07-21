package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.CompanyService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.Password;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.time.LocalDate;

import static umc.stockoneqback.global.utils.PasswordEncoderUtils.ENCODER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserFindService userFindService;
    private final StoreService storeService;
    private final CompanyService companyService;

    @Transactional
    public Long saveManager(User user, Long storeId) {
        validateDuplicateLoginId(user.getLoginId());

        Store store = storeService.findById(storeId);
        userRepository.save(user);
        store.updateStoreManager(user);

        return user.getId();
    }

    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw BaseException.type(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    @Transactional
    public Long savePartTimer(User user, String storeName, String storeCode) {
        validateDuplicateLoginId(user.getLoginId());
        Store store = storeService.findByName(storeName);
        validateStoreCode(storeCode, store.getCode());

        userRepository.save(user);
        store.updateStorePartTimers(user);

        return user.getId();
    }

    private void validateStoreCode(String userCode, String savedCode) {
        if (!userCode.equals(savedCode)) {
            throw BaseException.type(UserErrorCode.INVALID_STORE_CODE);
        }
    }

    @Transactional
    public Long saveSupervisor(User user, String companyName, String companyCode) {
        validateDuplicateLoginId(user.getLoginId());
        Company company = companyService.findByName(companyName);
        validateCompanyCode(companyCode, company.getCode());

        userRepository.save(user);
        company.addEmployees(user);

        return user.getId();
    }

    private void validateCompanyCode(String userCode, String savedCode) {
        if (!userCode.equals(savedCode)) {
            throw BaseException.type(UserErrorCode.INVALID_COMPANY_CODE);
        }
    }

    @Transactional
    public void updateInformation(Long userId, String name, LocalDate birth, String email, String loginId, String password, String phoneNumber) {
        User user = userFindService.findById(userId);
        user.updateInformation(Email.from(email), loginId, Password.encrypt(password, ENCODER), name, birth, phoneNumber);
    }
}
