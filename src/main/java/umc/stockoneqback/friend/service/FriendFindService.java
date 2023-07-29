package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.friend.repository.FriendRepository;
import umc.stockoneqback.friend.service.dto.SearchUserResponse;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendFindService {
    private final UserFindService userFindService;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public Friend findBySenderIdAndReceiverId(Long senderId, Long receiverId) {
        return friendRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND));
    }

    @Transactional
    public List<SearchUserResponse> searchFriends(Long userId, String searchName) throws IOException {
        User user = userFindService.findById(userId);
        validateSearchFriendsRole(user);

        List<User> userResult = userRepository.searchUserByName(searchName);

        return listToResponse(userResult);
    }

    private void validateSearchFriendsRole(User user) {
        if (user.getRole() != Role.MANAGER && user.getRole() != Role.SUPERVISOR) {
            throw BaseException.type(UserErrorCode.USER_IS_NOT_ALLOWED_TO_SEARCH);
        }
    }

    private List<SearchUserResponse> listToResponse(List<User> users) throws IOException{
        List<SearchUserResponse> userList = new ArrayList<>();

        for (User user : users) {
            SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                    .name(user.getName())
                    .phoneNumber(user.getPhoneNumber())
                    .company(user.getCompany())
                    .build();
            userList.add(searchUserResponse);
        }

        return userList;
    }
}
