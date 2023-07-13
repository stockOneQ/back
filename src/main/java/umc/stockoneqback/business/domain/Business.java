package umc.stockoneqback.business.domain;

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
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id")
    private Long id;

    private Status status;

    /*
    * TODO : User Table 에서 user_id (점장, 슈퍼바이저) 외래키 매핑
    * */
}
