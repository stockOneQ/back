package umc.stockoneqback.friend.service;

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

public class FriendProductService {
    private UserFindService userFindService;
    private StoreService storeService;
    private ProductOthersService productOthersService;
    private FriendService friendService;

    @Transactional
    public List<SearchProductOthersResponse> searchProductOthers
            (Long userId, Long friendId, String storeConditionValue, String productName) throws IOException {
        User friend = checkRelation(userId, friendId);
        Store friendStore = storeService.findByUser(friend);
        return productOthersService.searchProductOthers(friendStore, storeConditionValue, productName);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers
            (Long userId, Long friendId, String storeConditionValue) throws IOException {
        User friend = checkRelation(userId, friendId);
        Store friendStore = storeService.findByUser(friend);
        return productOthersService.getTotalProductOthers(friendStore, storeConditionValue);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfCategoryProductOthers
            (Long userId, Long friendId, String storeConditionValue, Long productId, String category) throws IOException {
        User friend = checkRelation(userId, friendId);
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

    private User checkRelation(Long userId, Long friendId) {
        User user = userFindService.findById(userId);
        User friend = userFindService.findById(friendId);
        friendService.validateNotFriend(user, friend);
        return friend;
    }
}
