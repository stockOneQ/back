package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.Share;

@Getter
@RequiredArgsConstructor
public enum ShareFixture {
    SHARE_1("제목:1", null, "내용1", Category.ANNOUNCEMENT),
    SHARE_2("제목2", null, "내용2", Category.ANNOUNCEMENT),
    SHARE_3("제목3", null, "내용3", Category.ANNOUNCEMENT),
    SHARE_4("제목:4", null, "내용4", Category.ANNOUNCEMENT),
    SHARE_5("제목5", null, "내용5", Category.RECIPE),
    SHARE_6("제목6", null, "내용6", Category.RECIPE),
    SHARE_7("제목:7", null, "내용7", Category.RECIPE),
    ;

    private final String title;
    private final String file;
    private final String content;
    private final Category category;

    public Share toShare(Business business) {
        return Share.createShare(title, file, content, category, business);
    }
}
