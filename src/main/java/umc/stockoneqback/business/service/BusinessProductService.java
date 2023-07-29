package umc.stockoneqback.business.service;

import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.service.ProductOthersService;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.List;

public class BusinessProductService {
    private UserFindService userFindService;
    private StoreService storeService;
    private ProductOthersService productOthersService;
    private BusinessService businessService;

    @Transactional
    public List<SearchProductOthersResponse> searchProductOthers
            (Long supervisorId, Long managerId, String storeConditionValue, String productName) throws IOException {
        User manager = checkRelation(supervisorId, managerId);
        Store managerStore = storeService.findByUser(manager);
        return productOthersService.searchProductOthers(managerStore, storeConditionValue, productName);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers
            (Long supervisorId, Long managerId, String storeConditionValue) throws IOException {
        User manager = checkRelation(supervisorId, managerId);
        Store managerStore = storeService.findByUser(manager);
        return productOthersService.getTotalProductOthers(managerStore, storeConditionValue);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfCategoryProductOthers
            (Long supervisorId, Long managerId, String storeConditionValue, Long productId, String category) throws IOException {
        User manager = checkRelation(supervisorId, managerId);
        Store managerStore = storeService.findByUser(manager);
        switch (category) {
            case "All" -> {
                return productOthersService.getListOfAllProductOthers(managerStore, storeConditionValue, productId);
            }
            case "Pass" -> {
                return productOthersService.getListOfPassProductOthers(managerStore, storeConditionValue, productId);
            }
            case "Close" -> {
                return productOthersService.getListOfCloseProductOthers(managerStore, storeConditionValue, productId);
            }
            case "Lack" -> {
                return productOthersService.getListOfLackProductOthers(managerStore, storeConditionValue, productId);
            }
        }
        throw BaseException.type(ProductErrorCode.NOT_FOUND_CATEGORY);
    }

    private User checkRelation(Long supervisorId, Long managerId) {
        User supervisor = userFindService.findById(supervisorId);
        User manager = userFindService.findById(managerId);
        businessService.validateNotExist(supervisor, manager);
        return manager;
    }
}
