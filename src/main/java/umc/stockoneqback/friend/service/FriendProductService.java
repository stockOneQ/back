package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.product.service.ProductOthersService;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.service.StoreService;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendProductService {
    private final UserFindService userFindService;
    private final StoreService storeService;
    private final ProductOthersService productOthersService;
    private final FriendService friendService;
    private final FriendFindService friendFindService;

    @Transactional
    public List<SearchProductOthersResponse> searchProductOthers(Long userId1, Long userId2, String storeConditionValue,
                                                                 String productName) throws IOException {
        User user1 = userFindService.findById(userId1);
        User user2 = userFindService.findById(userId2);

        friendService.validateNotFriend(user1, user2);
        friendService.validateRequestStatus(friendFindService.findByUserId(userId1, userId2));

        Store user2Store = storeService.findByUser(user2);
        return productOthersService.searchProductOthers(user2Store, storeConditionValue, productName);
    }

    @Transactional
    public List<GetTotalProductResponse> getTotalProductOthers(Long userId1, Long userId2, String storeConditionValue) {
        User user1 = userFindService.findById(userId1);
        User user2 = userFindService.findById(userId2);

        friendService.validateNotFriend(user1, user2);
        friendService.validateRequestStatus(friendFindService.findByUserId(userId1, userId2));

        Store user2Store = storeService.findByUser(user2);
        return productOthersService.getTotalProductOthers(user2Store, storeConditionValue);
    }

    @Transactional
    public List<SearchProductOthersResponse> getListOfSearchProductOthers(Long userId1, Long userId2, String storeConditionValue,
                                                                          Long productId, String searchConditionValue) throws IOException {
        User user1 = userFindService.findById(userId1);
        User user2 = userFindService.findById(userId2);

        friendService.validateNotFriend(user1, user2);
        friendService.validateRequestStatus(friendFindService.findByUserId(userId1, userId2));

        Store user2Store = storeService.findByUser(user2);
        return productOthersService.getListOfSearchProductOthers(user2Store, storeConditionValue, searchConditionValue, productId);
    }
}
