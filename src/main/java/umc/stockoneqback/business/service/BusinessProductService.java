package umc.stockoneqback.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.exception.GlobalErrorCode;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.product.service.ProductOthersService;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BusinessProductService {
    private final UserFindService userFindService;
    private final StoreService storeService;
    private final ProductOthersService productOthersService;
    private final BusinessService businessService;

    @Transactional
    public List<SearchProductOthersResponse> searchProductOthers
            (Long supervisorId, Long managerId, String storeConditionValue, String productName) throws IOException {
        User supervisor = isSupervisor(supervisorId);
        User manager = checkRelation(supervisor, managerId);
        Store managerStore = storeService.findByUser(manager);
        return productOthersService.searchProductOthers(managerStore, storeConditionValue, productName);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers
            (Long supervisorId, Long managerId, String storeConditionValue) throws IOException {
        User supervisor = isSupervisor(supervisorId);
        User manager = checkRelation(supervisor, managerId);
        Store managerStore = storeService.findByUser(manager);
        return productOthersService.getTotalProductOthers(managerStore, storeConditionValue);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfSearchProductOthers
            (Long supervisorId, Long managerId, String storeConditionValue, Long productId, String searchConditionValue) throws IOException {
        User supervisor = isSupervisor(supervisorId);
        User manager = checkRelation(supervisor, managerId);
        Store managerStore = storeService.findByUser(manager);
        return productOthersService.getListOfSearchProductOthers(managerStore, storeConditionValue, searchConditionValue, productId);
    }

    User isSupervisor(Long userId) {
        User user = userFindService.findById(userId);
        if (user.getRole() == Role.SUPERVISOR)
            return user;
        if (Arrays.stream(Role.values()).anyMatch(role -> role.equals(user.getRole())))
            throw BaseException.type(GlobalErrorCode.INVALID_USER);
        throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
    }

    User checkRelation(User supervisor, Long managerId) {
        User manager = userFindService.findById(managerId);
        businessService.validateNotExist(supervisor, manager);
        return manager;
    }
}
