package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.friend.repository.FriendRepository;
import umc.stockoneqback.global.exception.BaseException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendFindService {
    private final FriendRepository friendRepository;

    public Friend findBySenderIdAndReceiverId(Long senderId, Long receiverId) {
        return friendRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND));
    }
}
