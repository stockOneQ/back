package umc.stockoneqback.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.role.domain.company.Company;
import umc.stockoneqback.role.domain.store.Store;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

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

    @Convert(converter = Role.RoleConverter.class)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "manager_store_id", referencedColumnName = "id")
    private Store managerStore;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    // 회원 탈퇴시 작성한 게시글 모두 삭제
    @OneToMany(mappedBy = "writer", cascade = PERSIST, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();

    private User(Email email, String loginId, Password password, String username, LocalDate birth, String phoneNumber, Role role) {
        this.email = email;
        this.loginId = loginId;
        this.password = password;
        this.name = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.birth = birth;
        this.status = Status.NORMAL;
    }

    public static User createUser(Email email, String loginId, Password password, String username, LocalDate birth, String phoneNumber, Role role) {
        return new User(email, loginId, password, username, birth, phoneNumber, role);
    }

    public void registerManagerStore(Store store) {
        this.managerStore = store;
    }

    public void registerCompany(Company company) {
        this.company = company;
    }

    public void updateInformation(Email email, String loginId, Password password, String username, LocalDate birth, String phoneNumber) {
        this.email = email;
        this.loginId = loginId;
        this.password = password;
        this.name = username;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
    }
}
