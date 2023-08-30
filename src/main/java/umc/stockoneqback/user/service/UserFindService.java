package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.business.domain.BusinessRepository;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendRepository;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.domain.search.UserSearchType;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.infra.query.dto.FindManager;
import umc.stockoneqback.user.service.dto.response.FindManagerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFindService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final BusinessRepository businessRepository;

    private static final int PAGE_SIZE = 7;
    private static String relationStatus;

    public User findById(Long userId) {
        return userRepository.findByIdAndStatus(userId, Status.NORMAL)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginIdAndStatus(loginId, Status.NORMAL)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(Email email) {
        return userRepository.findByEmailAndStatus(email, Status.NORMAL)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public FindManagerResponse findFriendManagers(Long userId, Long lastUserId, String searchTypeValue, String searchWord) {
        UserSearchType userSearchType = UserSearchType.from(searchTypeValue);
        List<FindManager> managersBySearchType = userRepository.findManagersBySearchType(userId, userSearchType, searchWord);
        List<FindManager> findManagers = updateFriendRelationStatus(userId, managersBySearchType);

        int lastIndex = getLastIndex(findManagers, lastUserId);
        return configPaging(findManagers, lastIndex, PAGE_SIZE);
    }

    private List<FindManager> updateFriendRelationStatus(Long userId, List<FindManager> searchedManager) {
        List<FindManager> updateManagerList = new ArrayList<>();

        for (FindManager findManager : searchedManager) {
            Long findUserId = findManager.getId();
            Optional<Friend> findFriend = friendRepository.findBySenderIdAndReceiverId(userId, findUserId);
            Optional<Friend> findFriendReverse = friendRepository.findBySenderIdAndReceiverId(findUserId, userId);

            if (!findFriend.isEmpty())
                relationStatus = findFriend.get().getRelationStatus().getValue();
            else {
                if (!findFriendReverse.isEmpty()) {
                    relationStatus = findFriendReverse.get().getRelationStatus().getValue();

                } else {
                    relationStatus = RelationStatus.IRRELEVANT.getValue();
                }
            }

            FindManager findManagers = FindManager.builder()
                    .id(findManager.getId())
                    .name(findManager.getName())
                    .storeName(findManager.getStoreName())
                    .phoneNumber(findManager.getPhoneNumber())
                    .relationStatus(relationStatus)
                    .build();
            updateManagerList.add(findManagers);
        }
        return updateManagerList;
    }

    public FindManagerResponse findBusinessManagers(Long userId, Long lastUserId, String searchTypeValue, String searchWord) {
        UserSearchType userSearchType = UserSearchType.from(searchTypeValue);
        List<FindManager> managersBySearchType = userRepository.findManagersBySearchType(userId, userSearchType, searchWord);
        List<FindManager> findManagers = updateBusinessRelationStatus(userId, managersBySearchType);

        int lastIndex = getLastIndex(findManagers, lastUserId);
        return configPaging(findManagers, lastIndex, PAGE_SIZE);
    }

    private List<FindManager> updateBusinessRelationStatus(Long supervisorId, List<FindManager> searchedManager) {
        List<FindManager> updateManagerList = new ArrayList<>();

        for (FindManager findManager : searchedManager) {
            Long managerId = findManager.getId();
            Optional<Business> findBusiness = businessRepository.findBySupervisorIdAndManagerId(supervisorId, managerId);

            if (findBusiness.isPresent())
                relationStatus = findBusiness.get().getRelationStatus().getValue();
            else
                relationStatus = RelationStatus.IRRELEVANT.getValue();

            FindManager findManagers = FindManager.builder()
                    .id(findManager.getId())
                    .name(findManager.getName())
                    .storeName(findManager.getStoreName())
                    .phoneNumber(findManager.getPhoneNumber())
                    .relationStatus(relationStatus)
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
