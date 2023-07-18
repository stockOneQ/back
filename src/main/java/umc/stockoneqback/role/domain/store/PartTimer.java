package umc.stockoneqback.role.domain.store;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "part_timer")
public class PartTimer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_timer_id", referencedColumnName = "id", nullable = false)
    private User partTimer;

    private PartTimer(Store store, User partTimer) {
        this.store = store;
        this.partTimer = partTimer;
    }

    public static PartTimer createPartTimer(Store store, User partTimer) {
        return new PartTimer(store, partTimer);
    }
}
