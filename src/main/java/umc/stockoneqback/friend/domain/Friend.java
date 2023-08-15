package umc.stockoneqback.friend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.RelationStatus;
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

    @Convert(converter = RelationStatus.RelationConverter.class)
    private RelationStatus relationStatus;

    @Builder
    public Friend(User sender, User receiver, RelationStatus relationStatus) {
        this.sender = sender;
        this.receiver = receiver;
        this.relationStatus = relationStatus;
    }

    public static Friend createFriend(User sender, User receiver, RelationStatus relationStatus) {
        return new Friend(sender, receiver, relationStatus);
    }

    public void acceptFriend() {
        this.relationStatus = RelationStatus.ACCEPT;
    }
}