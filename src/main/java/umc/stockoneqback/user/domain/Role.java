package umc.stockoneqback.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    MANAGER("사장님"),
    PART_TIME("아르바이트생"),
    SUPERVISOR("슈퍼바이저")
    ;

    private final String value;
}
