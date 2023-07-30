package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.repository.FriendRepository;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.controller.dto.response.FindManagerResponse;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.domain.search.SearchCondition;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;
import java.util.stream.Collectors;

import static umc.stockoneqback.user.domain.search.SearchCondition.getSearchConditionByValue;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFindService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private static final int PAGE_SIZE = 6;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
    }

    public FindManagerResponse findManager(Long userId, String searchConditionValue, Long lastUserId, String searchWord) {
        List<FindManager> user = userRepository.findUserByUserId(userId);
        List<FindManager> friendUsers = friendRepository.findReceiversByUserId(userId, FriendStatus.ACCEPT);
        friendUsers.addAll(user);

        SearchCondition searchCondition = getSearchConditionByValue(searchConditionValue);
        List<FindManager> findUsers = null;
        switch (searchCondition) {
            case NAME -> findUsers = userRepository.findManagersByName(searchWord);
            case STORE -> findUsers = userRepository.findManagersByStoreName(searchWord);
            case ADDRESS -> findUsers = userRepository.findManagersByAddress(searchWord);
        }
        List<FindManager> searchedUsers = filterFindUsers(friendUsers, findUsers);

        int lastIndex = getLastIndex(searchedUsers, lastUserId);
        return configPaging(searchedUsers, lastIndex, PAGE_SIZE);
    }

    private List<FindManager> filterFindUsers(List<FindManager> friendUsers, List<FindManager> findUsers) {
        List<FindManager> searchedUsers = findUsers.stream()
                .filter(result ->
                        friendUsers.stream().noneMatch(target -> result.getPhoneNumber().equals(target.getPhoneNumber())))
                .collect(Collectors.toList());
        return searchedUsers;
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
