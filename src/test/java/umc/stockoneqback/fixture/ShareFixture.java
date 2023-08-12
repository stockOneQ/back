package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.share.domain.Category;
import umc.stockoneqback.share.domain.Share;

@Getter
@RequiredArgsConstructor
public enum ShareFixture {
    SHARE_0("공지제목0", null, "내용0", Category.ANNOUNCEMENT),
    SHARE_1("공지제목1", null, "내용1", Category.ANNOUNCEMENT),
    SHARE_2("공지제목2", null, "내용2", Category.ANNOUNCEMENT),
    SHARE_3("행사제목3", null, "내용3", Category.EVENT),
    SHARE_4("행사제목4", null, "내용4", Category.EVENT),
    SHARE_5("행사제목5", null, "내용5", Category.EVENT),
    SHARE_6("레시피제목6", null, "내용6", Category.RECIPE),
    SHARE_7("레시피제목7", null, "내용7", Category.RECIPE),
    SHARE_8("기타제목8", null, "내용8", Category.ETC),
    SHARE_9("기타제목9", null, "내용9", Category.ETC),
    ;

    private final String title;
    private final String file;
    private final String content;
    private final Category category;

    public Share toShare(Business business) {
        return Share.createShare(title, file, content, category, business);
    }
}
