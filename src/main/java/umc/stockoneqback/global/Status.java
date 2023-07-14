package umc.stockoneqback.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status implements EnumStandard {

    NORMAL("정상"),
    EXPIRED("소멸");

    private String value;

    @javax.persistence.Converter
    public static class Converter extends EnumConverter<Status> {
        public Converter() {
            super(Status.class);
        }
    }
}
