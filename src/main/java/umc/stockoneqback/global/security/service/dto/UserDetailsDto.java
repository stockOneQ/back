package umc.stockoneqback.global.security.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.user.domain.User;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserDetailsDto {
    private Long id;
    private String loginId;
    @JsonIgnore
    private String loginPassword;
    private String name;
    private List<String> roles;

    public UserDetailsDto(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.loginPassword = user.getPassword().getValue();
        this.name = user.getName();
        this.roles = List.of(user.getRole().getValue());
    }
}
