package umc.stockoneqback.global.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;

@Getter
@AllArgsConstructor
public enum Status implements EnumStandard {
    NORMAL("정상"),
    EXPIRED("소멸")
    ;

    private final String value;

    @javax.persistence.Converter
    public static class StatusConverter extends EnumConverter<Status> {
        public StatusConverter() {
            super(Status.class);
        }
    }
}
