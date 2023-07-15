package umc.stockoneqback.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.global.BaseTimeEntity;
import umc.stockoneqback.global.exception.ApplicationException;
import umc.stockoneqback.user.exception.UserErrorCode;

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

    @Embedded
    private Password password;

    private String username;

    private LocalDate birth;

    private String phoneNumber;

    @Embedded
    private Role role;

    private Boolean status;

    // 회원 탈퇴시 작성한 게시글 모두 삭제
    @OneToMany(mappedBy = "writer", cascade = PERSIST, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();

    private User(Email email, Password password, String username, LocalDate birth, String phoneNumber, Role role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = true;
    }

    public static User createUser(Email email, Password password, String username, LocalDate birth, String phoneNumber, String role) {
        return new User(email, password, username, birth, phoneNumber, roleStringToEnum(role));
    }

    private static Role roleStringToEnum(String roleString) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getValue().equals(roleString))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(UserErrorCode.ROLE_NOT_FOUND));
    }
}
