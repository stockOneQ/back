package umc.stockoneqback.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.repository.FriendRepository;
import umc.stockoneqback.user.service.dto.FindManager;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.FindManagerResponse;

import java.util.List;
import java.util.stream.Collectors;

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

    public FindManagerResponse findManager(Long userId, Long lastUserId, String name) {
        List<FindManager> friends = friendRepository.findReceiversByUserIdAndName(userId, name, FriendStatus.ACCEPT);
        List<FindManager> users = userRepository.findUsersByName(name);

        List<FindManager> searchedUsers = users.stream()
                .filter(user ->
                        friends.stream().noneMatch(target -> user.getPhoneNumber().equals(target.getPhoneNumber())))
                .collect(Collectors.toList());

        int lastIndex = getLastIndex(searchedUsers, lastUserId);
        return configPaging(searchedUsers, lastIndex, PAGE_SIZE);
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
