package umc.stockoneqback.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;

@Getter
@RequiredArgsConstructor
public enum Role implements EnumStandard {
    MANAGER("ROLE_MANAGER", "사장님"),
    PART_TIMER("ROLE_PART_TIMER", "아르바이트생"),
    SUPERVISOR("ROLE_SUPERVISOR", "슈퍼바이저")
    ;

    private final String authority;
    private final String description;

    @Override
    public String getValue() {
        return authority;
    }

    @javax.persistence.Converter
    public static class RoleConverter extends EnumConverter<Role> {
        public RoleConverter() {
            super(Role.class);
        }
    }
}
