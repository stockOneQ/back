package umc.stockoneqback.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import umc.stockoneqback.global.BaseTimeEntity;
import umc.stockoneqback.global.Status;
import umc.stockoneqback.role.domain.company.Company;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    private String loginId;

    @Embedded
    private Password password;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private String phoneNumber;

    private Role role;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    private Status status;

    private User(Email email, String loginId, Password password, String name, LocalDate birth, String phoneNumber, Role role) {
        this.email = email;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = Status.NORMAL;
    }

    public static User createUser(Email email, String loginId, Password password, String username, LocalDate birth, String phoneNumber, Role role) {
        return new User(email, loginId, password, username, birth, phoneNumber, role);
    }

    public void updateCompany(Company company) {
        this.company = company;
    }
}
