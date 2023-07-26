package umc.stockoneqback.business.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;

@Getter
@AllArgsConstructor
public enum Category implements EnumStandard {
    ANNOUNCEMENT("공지사항"),
    RECIPE("레시피"),
    EVENT("행사내용"),
    ETC("기타");

    private String value;

    @javax.persistence.Converter
    public static class CategoryConverter extends EnumConverter<Category> {
        public CategoryConverter() {
            super(Category.class);
        }
    }
}
