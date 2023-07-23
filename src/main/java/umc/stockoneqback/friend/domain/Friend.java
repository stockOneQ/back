package umc.stockoneqback.friend.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
@Getter
@Table(
        name="friend",
        uniqueConstraints={
                @UniqueConstraint(
                        name = "friend_uk",
                        columnNames={"res_user_id", "req_user_id"}
                )
        }
)
public class Friend extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "res_user_id")
    private User resUser; // 요청 받은 사람

    @ManyToOne
    @JoinColumn(name = "req_user_id")
    private User reqUser; // 요청 한 사람

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendStatus status;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Builder
    public Friend(User reqUser, User resUser, FriendStatus status) {
        this.reqUser = reqUser;
        this.resUser = resUser;
        this.status = status;
    }
}