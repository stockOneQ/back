package umc.stockoneqback.board.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.comment.service.CommentService;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Board [Service Layer] -> BoardService 테스트")
public class BoardServiceTest extends ServiceTest {
    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardFindService boardFindService;

    @Autowired
    private CommentService commentService;

    private User writer;
    private User not_writer;
    private Board board;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setup() {
        writer = userRepository.save(SAEWOO.toUser());
        not_writer = userRepository.save(ANNE.toUser());
        board = boardRepository.save(BOARD_0.toBoard(writer));
    }

    @Test
    @DisplayName("게시글 등록에 성공한다")
    void success() {
        // when
        Long boardId = boardService.create(writer.getId(), "제목", "내용");

        // then
        Board findBoard = boardRepository.findById(boardId).orElseThrow();
        assertAll(
                () -> assertThat(findBoard.getWriter().getId()).isEqualTo(writer.getId()),
                () -> assertThat(findBoard.getTitle()).isEqualTo("제목"),
                () -> assertThat(findBoard.getContent()).isEqualTo("내용"),
                () -> assertThat(findBoard.getCreatedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter)),
                () -> assertThat(findBoard.getModifiedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter))
        );
    }

    @Nested
    @DisplayName("게시글 수정")
    class update {
        @Test
        @DisplayName("다른 사람의 게시글은 수정할 수 없다")
        void throwExceptionByUserNotBoardWriter() {
            // when - then
            assertThatThrownBy(() -> boardService.update(not_writer.getId(),board.getId(), "제목2", "내용2"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(BoardErrorCode.USER_IS_NOT_BOARD_WRITER.getMessage());
        }

        @Test
        @DisplayName("게시글 수정에 성공한다")
        void success() {
            // given
            boardService.update(writer.getId(), board.getId(), "제목2", "내용2");

            // when
            Board findBoard = boardFindService.findById(board.getId());

            // then
            assertAll(
                    () -> assertThat(findBoard.getTitle()).isEqualTo("제목2"),
                    () -> assertThat(findBoard.getContent()).isEqualTo("내용2"),
                    () -> assertThat(findBoard.getModifiedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter))
            );
        }
    }
}
