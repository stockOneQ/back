package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.dto.SearchUserResponse;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.friend.repository.FriendRepository;
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
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserFindService userFindService;
    private final UserRepository userRepository;

    @Transactional
    public List<SearchUserResponse> searchFriends(Long userId, String searchName) throws IOException {
        User reqUser = userFindService.findById(userId);
        validateSearchFriendsRole(reqUser);

        List<User> users = userRepository.searchUserByName(searchName);
        if (users.isEmpty())
            throw BaseException.type(UserErrorCode.USER_NOT_FOUND);

        return listToResponse(users);
    }

    @Transactional
    public Friend sendFriendRequest(Long reqUserId, User friend) {
        User requester = userFindService.findById(reqUserId);
        validateFriendRequestUserRole(requester, friend);

        Friend newFriend = new Friend();
        newFriend.setReqUser(requester);
        newFriend.setFriend(friend);
        newFriend.setStatus(FriendStatus.REQUEST);

        return friendRepository.save(newFriend);
    }

    @Transactional
    public Friend updateFriendRequest(Long friendId, FriendStatus status) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND));
        friend.setStatus(status);

        return friendRepository.save(friend);
    }

    private void validateSearchFriendsRole(User user) {
        if (user.getRole() != Role.MANAGER && user.getRole() != Role.SUPERVISOR) {
            throw BaseException.type(FriendErrorCode.IS_A_PART_TIMER);
        }
    }

    private void validateFriendRequestUserRole(User requester, User friend) {
        if (requester.getRole() != Role.MANAGER || friend.getRole() != Role.MANAGER) {
            throw BaseException.type(FriendErrorCode.NOT_A_MANAGER);
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
