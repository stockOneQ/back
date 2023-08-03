package umc.stockoneqback.business.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "business")
public class Business extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    @Builder
    public Business(User manager, User supervisor) {
        this.manager = manager;
        this.supervisor = supervisor;
        this.status = Status.NORMAL;
    }
}
