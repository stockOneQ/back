package umc.stockoneqback.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.exception.UserErrorCode;
import org.springframework.format.annotation.DateTimeFormat;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.role.domain.company.Company;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

    private Role role;

    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    private Status status;

    // 회원 탈퇴시 작성한 게시글 모두 삭제
    @OneToMany(mappedBy = "writer", cascade = PERSIST, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();

    private User(Email email, String loginId, Password password, String username, LocalDate birth, String phoneNumber, Role role) {
        this.email = email;
        this.loginId = loginId;
        this.password = password;
        this.name = username;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = Status.NORMAL;
    }

    public static User createUser(Email email, String loginId, Password password, String username, LocalDate birth, String phoneNumber, Role role) {
        return new User(email, loginId, password, username, birth, phoneNumber, role);
    }

    private static Role roleStringToEnum(String roleString) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getValue().equals(roleString))
                .findFirst()
                .orElseThrow(() -> new BaseException(UserErrorCode.ROLE_NOT_FOUND));
    }
    public void updateCompany(Company company) {
        this.company = company;
    }
}
