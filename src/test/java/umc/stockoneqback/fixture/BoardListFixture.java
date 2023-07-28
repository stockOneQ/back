package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardListFixture {
    BOARD_1("제목1", "파일1", "내용1", 5),
    BOARD_2("제목2", "파일2", "내용2", 4),
    BOARD_3("제목3", "파일3", "내용3", 3),
    BOARD_4("제목4", "파일4", "내용4", 2),
    BOARD_5("제목5", "파일5", "내용5", 1),
    BOARD_6("제목6", "파일6", "내용6", 1),
    BOARD_7("제목7", "파일7", "내용7", 2),
    BOARD_8("제목8", "파일8", "내용8", 3),
    BOARD_9("제목9", "파일9", "내용9", 4),
    ;

    private final String title;
    private final String file;
    private final String content;
    private final int hit;
}
