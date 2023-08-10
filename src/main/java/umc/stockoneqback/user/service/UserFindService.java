package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.repository.FriendRepository;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.domain.search.SearchType;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.infra.query.dto.FindManager;
import umc.stockoneqback.user.service.dto.response.FindManagerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static umc.stockoneqback.user.domain.search.SearchType.findSearchTypeByValue;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFindService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private static final int PAGE_SIZE = 7;
    private String friendStatus;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(Email email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public FindManagerResponse findManagers(Long userId, Long lastUserId, String searchTypeValue, String searchWord) {
        SearchType searchType = findSearchTypeByValue(searchTypeValue);
        List<FindManager> managersBySearchType = userRepository.findManagersBySearchType(searchType, searchWord);
        List<FindManager> findManagers = updateFriendStatus(userId, managersBySearchType);

        int lastIndex = getLastIndex(findManagers, lastUserId);
        return configPaging(findManagers, lastIndex, PAGE_SIZE);
    }

    private List<FindManager> updateFriendStatus(Long userId, List<FindManager> searchedManager) {
        List<FindManager> updateManagerList = new ArrayList<>();

        for (FindManager findManager : searchedManager) {
            Long findUserId = findManager.getId();
            Optional<Friend> findFriend = friendRepository.findBySenderIdAndReceiverId(userId, findUserId);
            Optional<Friend> findFriendReverse = friendRepository.findBySenderIdAndReceiverId(findUserId, userId);

            if (!findFriend.isEmpty())
                friendStatus = findFriend.get().getFriendStatus().getValue();
            else {
                if (!findFriendReverse.isEmpty())
                    friendStatus = findFriendReverse.get().getFriendStatus().getValue();
                else friendStatus = "친구 아님";
            }

            FindManager findManagers = FindManager.builder()
                    .id(findManager.getId())
                    .name(findManager.getName())
                    .storeName(findManager.getStoreName())
                    .phoneNumber(findManager.getPhoneNumber())
                    .friendStatus(friendStatus)
                    .build();
            updateManagerList.add(findManagers);
        }

        return updateManagerList;
    }

    private int getLastIndex(List<FindManager> searchedUsers, Long lastUserId) {
        return searchedUsers.indexOf(
                searchedUsers.stream()
                        .filter(user -> user.getId().equals(lastUserId))
                        .findFirst()
                        .orElse(null)
        );
    }

    private FindManagerResponse configPaging(List<FindManager> searchedUsers, int lastIndex, int size) {
        if (lastIndex + 1 + size >= searchedUsers.size()) {
            return new FindManagerResponse(searchedUsers.subList(lastIndex + 1, searchedUsers.size()));
        }
        return new FindManagerResponse(searchedUsers.subList(lastIndex + 1, lastIndex + 1 + size));
    }
}
