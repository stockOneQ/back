package umc.stockoneqback.share.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.share.exception.ShareErrorCode;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Category implements EnumStandard {
    ANNOUNCEMENT("공지사항"),
    RECIPE("레시피"),
    EVENT("행사내용"),
    ETC("기타");

    private final String value;

    public static Category from(String value) {
        return Arrays.stream(values())
                .filter(category -> category.value.equals(value))
                .findFirst()
                .orElseThrow(() -> BaseException.type(ShareErrorCode.NOT_FOUND_CATEGORY));
    }

    @javax.persistence.Converter
    public static class CategoryConverter extends EnumConverter<Category> {
        public CategoryConverter() {
            super(Category.class);
        }
    }
}
