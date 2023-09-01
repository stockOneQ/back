package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendRepository;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.global.exception.BaseException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendFindService {
    private final FriendRepository friendRepository;

    public Friend findBySenderIdAndReceiverId(Long userId1, Long userId2) {
        return friendRepository.findBySenderIdAndReceiverId(userId1, userId2)
                .orElseThrow(() -> BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND));
    }

    public Friend findByUserId(Long userId1, Long userId2) {
        return friendRepository.findBySenderIdAndReceiverId(userId1, userId2)
                .or(() -> friendRepository.findBySenderIdAndReceiverId(userId2, userId1))
                .orElseThrow(() -> BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND));
    }
}
