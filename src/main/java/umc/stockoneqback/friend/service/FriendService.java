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
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserFindService userFindService;
    private final FriendFindService friendFindService;

    @Transactional
    public Long requestFriend(Long senderId, Long receiverId) {
        User sender = userFindService.findById(senderId);
        User receiver = userFindService.findById(receiverId);

        validateManager(sender);
        validateManager(receiver);
        validateAlreadyFriend(sender, receiver);

        return friendRepository.save(Friend.createFriend(sender, receiver, FriendStatus.REQUEST)).getId();
    }

    @Transactional
    public void cancelFriend(Long senderId, Long receiverId) {
        Friend friend = friendFindService.findBySenderIdAndReceiverId(senderId, receiverId);
        friendRepository.delete(friend);
    }

    @Transactional
    public Long acceptFriend(Long senderId, Long receiverId) {
        Friend friend = friendFindService.findBySenderIdAndReceiverId(senderId, receiverId);
        friend.acceptFriend();

        return friend.getId();
    }

    @Transactional
    public void rejectFriend(Long senderId, Long receiverId) {
        Friend friend = friendFindService.findBySenderIdAndReceiverId(senderId, receiverId);
        friendRepository.delete(friend);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendUserId) {
        Friend friend = friendRepository.findBySenderIdAndReceiverId(userId, friendUserId)
                .or(() -> friendRepository.findBySenderIdAndReceiverId(friendUserId, userId))
                .orElseThrow(() -> BaseException.type(FriendErrorCode.ALREADY_EXIST_FRIEND));

        friendRepository.delete(friend);
    }

    private void validateManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw BaseException.type(UserErrorCode.USER_IS_NOT_MANAGER);
        }
    }

    private void validateAlreadyFriend(User sender, User receiver) {
        if (friendRepository.existsBySenderAndReceiver(sender, receiver) || friendRepository.existsBySenderAndReceiver(receiver, sender)) {
            throw BaseException.type(FriendErrorCode.ALREADY_EXIST_FRIEND);
        }
    }
}
