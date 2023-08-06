package umc.stockoneqback.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.user.domain.User;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Getter
@RequiredArgsConstructor
public enum BoardFixture {
    BOARD_0("제목0", "내용0", 9, now()),
    BOARD_1("제목1", "내용1", 8, now().plusDays(1L)),
    BOARD_2("제목2", "내용2", 7, now().plusDays(2L)),
    BOARD_3("제목3", "내용3", 6, now().plusDays(3L)),
    BOARD_4("제목4", "내용4", 5, now().plusDays(4L)),
    BOARD_5("제목5", "내용5", 4, now().plusDays(5L)),
    BOARD_6("제목6", "내용6", 3, now().plusDays(6L)),
    BOARD_7("제목7", "내용7", 2, now().plusDays(7L)),
    BOARD_8("제목8", "내용8", 1, now().plusDays(8L)),
    BOARD_9("제목9", "내용9", 0, now().plusDays(9L)),
    ;

    private final String title;
    private final String content;
    private final int hit;
    private final LocalDateTime createdDate;

    public Board toBoard(User writer) {
        return Board.createBoard(writer, title, content);
    }
}
