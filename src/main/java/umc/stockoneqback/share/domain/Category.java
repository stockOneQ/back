package umc.stockoneqback.share.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.share.exception.ShareErrorCode;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, Category> map = new HashMap<>();
    static {
        for (Category category : values()) {
            map.put(category.value, category);
        }
    }

    public static Category findCategoryByValue(String categoryValue) {
        if (map.containsKey(categoryValue)) {
            return map.get(categoryValue);
        } else {
            throw BaseException.type(ShareErrorCode.NOT_FOUND_CATEGORY);
        }
    }
}
