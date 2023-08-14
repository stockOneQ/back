package umc.stockoneqback.global.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;

@Getter
@AllArgsConstructor
public enum RelationStatus implements EnumStandard {
    REQUEST("요청"),
    ACCEPT("수락"),
    IRRELEVANT("무관")
    ;

    private final String value;

    @javax.persistence.Converter
    public static class RelationConverter extends EnumConverter<RelationStatus> {
        public RelationConverter() {
            super(RelationStatus.class);
        }
    }
}
