package umc.stockoneqback.friend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="friend")
public class Friend extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Convert(converter = FriendStatus.FriendConverter.class)
    private FriendStatus friendStatus;

    @Builder
    public Friend(User sender, User receiver, FriendStatus friendStatus) {
        this.sender = sender;
        this.receiver = receiver;
        this.friendStatus = friendStatus;
    }

    public static Friend createFriend(User sender, User receiver, FriendStatus friendStatus) {
        return new Friend(sender, receiver, friendStatus);
    }

    public void acceptFriend() {
        this.friendStatus = FriendStatus.ACCEPT;
    }
}