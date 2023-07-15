package umc.stockoneqback.role.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.Status;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
/*
 * TODO : ADD "extends BaseTimeEntity"
 * */
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(name = "store_name")
    private String name;

    private String sector;

    @Column(name = "store_code")
    private String code;

    private String address;

    private Status status;
}
