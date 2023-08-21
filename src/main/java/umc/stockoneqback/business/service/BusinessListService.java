package umc.stockoneqback.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.business.infra.query.dto.BusinessList;
import umc.stockoneqback.business.service.dto.BusinessListResponse;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.user.service.dto.response.FindManagerResponse;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusinessListService {
    private final BusinessRepository businessRepository;
    private static final int SUPERVISOR_PAGE_SIZE = 8;
    private static final int MANAGER_PAGE_SIZE = 7;

    public BusinessListResponse getSupervisor(Long userId, Long lastUserId) {
        List<BusinessList> supervisors = businessRepository.findSupervisorByManagerIdAndRelationStatus(userId, RelationStatus.ACCEPT);

        int lastIndex = getLastIndex(supervisors, lastUserId);
        return configPaging(supervisors, lastIndex, SUPERVISOR_PAGE_SIZE);
    }

    private int getLastIndex(List<BusinessList> supervisors, Long lastUserId) {
        return supervisors.indexOf(
                supervisors.stream()
                        .filter(user -> user.getId().equals(lastUserId))
                        .findFirst()
                        .orElse(null)
        );
    }

    private BusinessListResponse configPaging(List<BusinessList> searchedUsers, int lastIndex, int size) {
        if (lastIndex + 1 + size >= searchedUsers.size()) {
            return new BusinessListResponse(searchedUsers.subList(lastIndex + 1, searchedUsers.size()));
        }
        return new BusinessListResponse(searchedUsers.subList(lastIndex + 1, lastIndex + 1 + size));
    }
}
