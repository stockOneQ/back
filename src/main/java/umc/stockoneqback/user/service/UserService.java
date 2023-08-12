package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.CompanyService;
import umc.stockoneqback.role.service.PartTimerService;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StoreService storeService;
    private final CompanyService companyService;
    private final PartTimerService partTimerService;

    @Transactional
    public Long saveManager(User user, Long storeId) {
        validateDuplicate(user.getLoginId(), user.getEmail());

        Store store = storeService.findById(storeId);
        userRepository.save(user);
        store.updateStoreManager(user);

        return user.getId();
    }

    @Transactional
    public Long savePartTimer(User user, String storeName, String storeCode) {
        validateDuplicate(user.getLoginId(), user.getEmail());
        Store store = storeService.findByName(storeName);
        validateStoreCode(storeCode, store.getCode());

        userRepository.save(user);
        PartTimer partTimer = store.updateStorePartTimers(user);
        partTimerService.savePartTimer(partTimer);

        return user.getId();
    }

    private void validateStoreCode(String userCode, String savedCode) {
        if (!userCode.equals(savedCode)) {
            throw BaseException.type(UserErrorCode.INVALID_STORE_CODE);
        }
    }

    @Transactional
    public Long saveSupervisor(User user, String companyName, String companyCode) {
        validateDuplicate(user.getLoginId(), user.getEmail());
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

    private void validateDuplicate(String loginId, Email email) {
        validateDuplicateLoginId(loginId);
        validateDuplicateEmail(email);
    }

    private void validateDuplicateLoginId(String loginId) {
        if (userRepository.existsByLoginIdAndStatus(loginId, Status.NORMAL)) {
            throw BaseException.type(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private void validateDuplicateEmail(Email email) {
        if (userRepository.existsByEmailAndStatus(email, Status.NORMAL)) {
            throw BaseException.type(UserErrorCode.DUPLICATE_EMAIL);
        }
    }
}
