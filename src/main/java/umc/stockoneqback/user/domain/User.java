package umc.stockoneqback.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.BaseTimeEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
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
                .orElse(null);
    }
}
