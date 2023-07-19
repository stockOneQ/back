package umc.stockoneqback.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.global.enumconfig.EnumConverter;
import umc.stockoneqback.global.enumconfig.EnumStandard;

@Getter
@RequiredArgsConstructor
public enum Role implements EnumStandard {
    MANAGER("사장님"),
    PART_TIMER("아르바이트생"),
    SUPERVISOR("슈퍼바이저")
    ;

    private final String value;

    @javax.persistence.Converter
    public static class RoleConverter extends EnumConverter<Role> {
        public RoleConverter() {
            super(Role.class);
        }
    }
}
