package umc.stockoneqback.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.friend.domain.FriendRepository;
import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;
import umc.stockoneqback.friend.service.dto.FriendAssembler;
import umc.stockoneqback.global.base.RelationStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendInformationService {
    private final FriendRepository friendRepository;
    private static final int FRIENDS_PAGE_SIZE = 8;
    private static final int WAITING_FRIENDS_PAGE_SIZE = 3;
    private static final int REQUESTED_FRIENDS_PAGE_SIZE = 6;

    public FriendAssembler getFriends(Long userId, Long lastUserId) {
        List<FriendInformation> receivers = friendRepository.findReceiversByUserIdAndRelationStatus(userId, RelationStatus.ACCEPT);
        List<FriendInformation> senders = friendRepository.findSendersByUserIdAndRelationStatus(userId, RelationStatus.ACCEPT);

        List<FriendInformation> friends = new ArrayList<>(receivers);
        friends.addAll(senders);
        friends.sort(Comparator.comparing(FriendInformation::getLastModifiedDate).reversed());

        int lastIndex = getLastIndex(friends, lastUserId);
        return getFriendAssembler(friends, lastIndex, FRIENDS_PAGE_SIZE);
    }

    public FriendAssembler getWaitingFriends(Long userId, Long lastUserId) {
        List<FriendInformation> receivers = friendRepository.findReceiversByUserIdAndRelationStatus(userId, RelationStatus.REQUEST);

        int lastIndex = getLastIndex(receivers, lastUserId);
        return getFriendAssembler(receivers, lastIndex, WAITING_FRIENDS_PAGE_SIZE);
    }

    public FriendAssembler getRequestedFriends(Long userId, Long lastUserId) {
        List<FriendInformation> senders = friendRepository.findSendersByUserIdAndRelationStatus(userId, RelationStatus.REQUEST);

        int lastIndex = getLastIndex(senders, lastUserId);
        return getFriendAssembler(senders, lastIndex, REQUESTED_FRIENDS_PAGE_SIZE);
    }

    private int getLastIndex(List<FriendInformation> friends, Long lastUserId) {
        return friends.indexOf(
                friends.stream()
                        .filter(friendInformation -> friendInformation.getId().equals(lastUserId))
                        .findFirst()
                        .orElse(null)
        );
    }

    private FriendAssembler getFriendAssembler(List<FriendInformation> friends, int lastIndex, int size) {
        if (lastIndex + 1 + size >= friends.size()) {
            return new FriendAssembler(friends.subList(lastIndex + 1, friends.size()));
        }
        return new FriendAssembler(friends.subList(lastIndex + 1, lastIndex + 1 + size));
    }
}
