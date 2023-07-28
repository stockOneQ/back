package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.user.domain.User;

@Getter
@RequiredArgsConstructor
public enum BoardFixture {
    BOARD_0("제목0", "파일0", "내용0"),
    BOARD_1("제목1", "파일1", "내용1"),
    BOARD_2("제목2", "파일2", "내용2"),
    BOARD_3("제목3", "파일3", "내용3")
    ;

    private final String title;
    private final String file;
    private final String content;

    public Board toBoard(User writer) {
        return Board.createBoard(writer, title, file, content);
    }
}
