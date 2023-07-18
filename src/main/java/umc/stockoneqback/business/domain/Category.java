package umc.stockoneqback.business.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.EnumConverter;
import umc.stockoneqback.global.EnumStandard;

@Getter
@AllArgsConstructor
public enum Category implements EnumStandard {
    ANNOUNCEMENT("공지사항"),
    RECIPE("레시피"),
    EVENT("행사내용"),
    ETC("기타");

    private String value;

    @javax.persistence.Converter
    public static class Converter extends EnumConverter<Category> {
        public Converter() {
            super(Category.class);
        }
    }
}
