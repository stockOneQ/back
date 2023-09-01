package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendRepository;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

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

        validateSameUser(senderId, receiverId);
        validateAlreadyFriend(sender, receiver);

        return friendRepository.save(Friend.createFriend(sender, receiver, RelationStatus.REQUEST)).getId();
    }

    private void validateSameUser(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw BaseException.type(FriendErrorCode.SELF_FRIEND_REQUEST_NOT_ALLOWED);
        }
    }

    private void validateAlreadyFriend(User sender, User receiver) {
        if (friendRepository.existsBySenderAndReceiver(sender, receiver) || friendRepository.existsBySenderAndReceiver(receiver, sender)) {
            throw BaseException.type(FriendErrorCode.ALREADY_EXIST_FRIEND);
        }
    }

    @Transactional
    public void cancelFriend(Long senderId, Long receiverId) {
        Friend friend = friendFindService.findBySenderIdAndReceiverId(senderId, receiverId);
        validateAcceptStatus(friend);
        friendRepository.delete(friend);
    }

    @Transactional
    public Long acceptFriend(Long senderId, Long receiverId) {
        Friend friend = friendFindService.findBySenderIdAndReceiverId(senderId, receiverId);
        validateAcceptStatus(friend);

        friend.acceptFriend();
        return friend.getId();
    }

    @Transactional
    public void rejectFriend(Long senderId, Long receiverId) {
        Friend friend = friendFindService.findBySenderIdAndReceiverId(senderId, receiverId);
        validateAcceptStatus(friend);

        friendRepository.delete(friend);
    }

    public void validateAcceptStatus(Friend friend) {
        if (friend.getRelationStatus().equals(RelationStatus.ACCEPT)) {
            throw BaseException.type(FriendErrorCode.STATUS_IS_ACCEPT);
        }
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendUserId) {
        Friend friend = friendRepository.findBySenderIdAndReceiverId(userId, friendUserId)
                .or(() -> friendRepository.findBySenderIdAndReceiverId(friendUserId, userId))
                .orElseThrow(() -> BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND));
        validateRequestStatus(friend);

        friendRepository.delete(friend);
    }

    @Transactional
    public void deleteFriendByUser(User user) {
        friendRepository.deleteFriendByUser(user);
    }

    public void validateRequestStatus(Friend friend) {
        if (friend.getRelationStatus().equals(RelationStatus.REQUEST)) {
            throw BaseException.type(FriendErrorCode.STATUS_IS_REQUEST);
        }
    }

    public void validateNotFriend(User sender, User receiver) {
        if (!friendRepository.existsBySenderAndReceiver(sender, receiver) && !friendRepository.existsBySenderAndReceiver(receiver, sender)) {
            throw BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND);
        }
    }
}
