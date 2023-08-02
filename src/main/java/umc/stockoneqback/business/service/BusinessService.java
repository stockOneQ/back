package umc.stockoneqback.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.business.exception.BusinessErrorCode;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final UserFindService userFindService;

    @Transactional
    public void register(Long supervisorId, Long managerId) {
        User supervisor = userFindService.findById(supervisorId);
        User manager = userFindService.findById(managerId);

        validateAlreadyExist(supervisor, manager);

        businessRepository.save(new Business(manager, supervisor));
    }

    private void validateAlreadyExist(User supervisor, User manager) {
        if (businessRepository.existsBySupervisorAndManager(supervisor, manager)) {
            throw BaseException.type(BusinessErrorCode.ALREADY_EXIST_BUSINESS);
        }
    }

    @Transactional
    public void cancel(Long supervisorId, Long managerId) {
        User supervisor = userFindService.findById(supervisorId);
        User manager = userFindService.findById(managerId);

        validateNotExist(supervisor, manager);

        businessRepository.deleteBySupervisorAndManager(supervisor, manager);
    }

    void validateNotExist(User supervisor, User manager) {
        if (!businessRepository.existsBySupervisorAndManager(supervisor, manager)) {
            throw BaseException.type(BusinessErrorCode.BUSINESS_NOT_FOUND);
        }
    }
}
