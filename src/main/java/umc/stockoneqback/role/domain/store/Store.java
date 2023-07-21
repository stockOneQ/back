package umc.stockoneqback.role.domain.store;

import lombok.*;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.domain.User;

import javax.persistence.*;

import static umc.stockoneqback.global.base.Status.NORMAL;
import static umc.stockoneqback.global.utils.RandomCodeGenerator.generateRandomCode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "store")
public class Store extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String sector;

    private String code;

    private String address;

    @OneToOne
    private User manager;

    @Embedded
    private PartTimers partTimers;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    @Builder
    public Store(String name, String sector, String code, String address) {
        this.name = name;
        this.sector = sector;
        this.code = code;
        this.address = address;
        this.manager = null;
        this.partTimers = PartTimers.createPartTimers();
        this.status = NORMAL;
    }

    public static Store createStore(String name, String sector, String address) {
        return new Store(name, sector, generateRandomCode(), address);
    }

    public void updateStoreManager(User manager) {
        this.manager = manager;
    }

    public void updateStorePartTimers(User partTimer) {
        partTimers.addPartTimer(PartTimer.createPartTimer(this, partTimer));
    }
}
