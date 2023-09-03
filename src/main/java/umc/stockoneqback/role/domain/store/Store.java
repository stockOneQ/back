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
    public Store(String name, String sector, String code, String address, User manager) {
        this.name = name;
        this.sector = sector;
        this.code = code;
        this.address = address;
        this.manager = manager;
        this.partTimers = PartTimers.createPartTimers();
        this.status = NORMAL;
    }

    public static Store createStore(String name, String sector, String address, User manager) {
        return new Store(name, sector, generateRandomCode(), address, manager);
    }

    public void updateManager(User manager) {
        this.manager = manager;
        manager.registerManagerStore(this);
    }

    public PartTimer updatePartTimer(User partTimer) {
        PartTimer newPartTimer = PartTimer.createPartTimer(this, partTimer);
        partTimers.addPartTimer(newPartTimer);
        return newPartTimer;
    }

    public void updatePartTimer(PartTimer partTimer) {
        partTimers.addPartTimer(partTimer);
    }

    public void deletePartTimer(PartTimer partTimer) {
        partTimers.deletePartTimer(partTimer);
    }

    public void deleteManager() {
        this.manager = null;
    }
}
