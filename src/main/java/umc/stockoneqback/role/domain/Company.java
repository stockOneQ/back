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
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @Column(name = "company_name")
    private String name;
    private String sector;
    @Column(name = "company_code")
    private String code;

    private Status status;

}
