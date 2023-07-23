package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.friend.repository.FriendRepository;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;

    public List<Friend> searchFriends(User user) {
        validateSearchFriendsRole(user);

        List<Friend> byUser = friendRepository.findByUser(user);
        List<Friend> resUser = new ArrayList<>();
        byUser.forEach(res -> {
            if(res.getReqUser().getRole() == Role.MANAGER){
                resUser.add(res);
            }
        });

        return friendRepository.findByUser(user);
    }

    @Transactional
    public Friend sendFriendRequest(User requester, User friend) {
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
}
