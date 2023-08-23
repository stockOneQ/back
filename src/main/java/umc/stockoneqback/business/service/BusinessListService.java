package umc.stockoneqback.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.business.infra.query.dto.BusinessList;
import umc.stockoneqback.business.service.dto.BusinessListResponse;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusinessListService {
    private final BusinessRepository businessRepository;
    private final UserFindService userFindService;

    private static final int SUPERVISOR_PAGE_SIZE = 8;
    private static final int MANAGER_PAGE_SIZE = 7;

    public BusinessListResponse getSupervisors(Long userId, Long lastUserId, String search) {
        validateIsManager(userId);
        List<BusinessList> supervisors = businessRepository.findSupervisorByManagerIdAndRelationStatus(userId, RelationStatus.ACCEPT, search);

        int lastIndex = getLastIndex(supervisors, lastUserId);
        return configPaging(supervisors, lastIndex, SUPERVISOR_PAGE_SIZE);
    }

    public BusinessListResponse getManagers(Long userId, Long lastUserId) {
        validateIsSupervisor(userId);
        List<BusinessList> managers = businessRepository.findManagerBySupervisorIdAndRelationStatus(userId, RelationStatus.ACCEPT);

        int lastIndex = getLastIndex(managers, lastUserId);
        return configPaging(managers, lastIndex, MANAGER_PAGE_SIZE);
    }

    private int getLastIndex(List<BusinessList> supervisors, Long lastUserId) {
        return supervisors.indexOf(
                supervisors.stream()
                        .filter(user -> user.getId().equals(lastUserId))
                        .findFirst()
                        .orElse(null)
        );
    }

    private BusinessListResponse configPaging(List<BusinessList> users, int lastIndex, int size) {
        if (lastIndex + 1 + size >= users.size()) {
            return new BusinessListResponse(users.subList(lastIndex + 1, users.size()));
        }
        return new BusinessListResponse(users.subList(lastIndex + 1, lastIndex + 1 + size));
    }

    private void validateIsManager(Long userId) {
        User user = userFindService.findById(userId);
        if (user.getRole() != Role.MANAGER) {
            throw BaseException.type(UserErrorCode.USER_NOT_ALLOWED);
        }
    }

    private void validateIsSupervisor(Long userId) {
        User user = userFindService.findById(userId);
        if (user.getRole() != Role.SUPERVISOR) {
            throw BaseException.type(UserErrorCode.USER_NOT_ALLOWED);
        }
    }
}
