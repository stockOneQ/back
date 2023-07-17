package umc.stockoneqback.business.domain;

import lombok.*;
import umc.stockoneqback.global.BaseTimeEntity;
import umc.stockoneqback.global.Status;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Business extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @Builder
    public Business(Status status, User manager, User supervisor) {
        this.status = status;
        this.manager = manager;
        this.supervisor = supervisor;
    }

}
