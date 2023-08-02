package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.base.GlobalErrorCode;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
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
public class FriendProductService {
    private final UserFindService userFindService;
    private final StoreService storeService;
    private final ProductOthersService productOthersService;
    private final FriendService friendService;

    @Transactional
    public List<SearchProductOthersResponse> searchProductOthers
            (Long userId, Long friendId, String storeConditionValue, String productName) throws IOException {
        User manager = isManager(userId);
        User friend = checkRelation(manager, friendId);
        Store friendStore = storeService.findByUser(friend);
        return productOthersService.searchProductOthers(friendStore, storeConditionValue, productName);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers
            (Long userId, Long friendId, String storeConditionValue) throws IOException {
        User manager = isManager(userId);
        User friend = checkRelation(manager, friendId);
        Store friendStore = storeService.findByUser(friend);
        return productOthersService.getTotalProductOthers(friendStore, storeConditionValue);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfCategoryProductOthers
            (Long userId, Long friendId, String storeConditionValue, Long productId, String category) throws IOException {
        User manager = isManager(userId);
        User friend = checkRelation(manager, friendId);
        Store friendStore = storeService.findByUser(friend);
        switch (category) {
            case "All" -> {
                return productOthersService.getListOfAllProductOthers(friendStore, storeConditionValue, productId);
            }
            case "Pass" -> {
                return productOthersService.getListOfPassProductOthers(friendStore, storeConditionValue, productId);
            }
            case "Close" -> {
                return productOthersService.getListOfCloseProductOthers(friendStore, storeConditionValue, productId);
            }
            case "Lack" -> {
                return productOthersService.getListOfLackProductOthers(friendStore, storeConditionValue, productId);
            }
        }
        throw BaseException.type(ProductErrorCode.NOT_FOUND_CATEGORY);
    }

    User isManager(Long userId) {
        User user = userFindService.findById(userId);
        if (user.getRole() == Role.MANAGER)
            return user;
        if (Arrays.stream(Role.values()).anyMatch(role -> role.equals(user.getRole())))
            throw BaseException.type(GlobalErrorCode.INVALID_USER_JWT);
        throw BaseException.type(UserErrorCode.ROLE_NOT_FOUND);
    }

    User checkRelation(User user, Long friendId) {
        User friend = userFindService.findById(friendId);
        friendService.validateNotFriend(user, friend);
        return friend;
    }
}
