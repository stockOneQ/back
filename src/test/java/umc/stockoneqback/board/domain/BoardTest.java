package umc.stockoneqback.board.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.fixture.UserFixture.WIZ;
import static umc.stockoneqback.global.base.Status.NORMAL;

@DisplayName("Board 도메인 테스트")
public class BoardTest {
    private final User[] writer = new User[2];
    private Board board;

    @BeforeEach
    void setUp() {
        writer[0] = SAEWOO.toUser();
        writer[1] = WIZ.toUser();
        board = BOARD_0.toBoard(writer[0]);
    }

    @Test
    @DisplayName("Board 생성에 성공한다")
    void success() {
        assertAll(
                () -> assertThat(board.getWriter()).isEqualTo(writer[0]),
                () -> assertThat(board.getTitle()).isEqualTo(BOARD_0.getTitle()),
                () -> assertThat(board.getContent()).isEqualTo(BOARD_0.getContent()),
                () -> assertThat(board.getHit()).isEqualTo(0),
                () -> assertThat(board.getStatus()).isEqualTo(NORMAL)
        );
    }

    @Test
    @DisplayName("Board에 Comment를 추가한다")
    void addComment() {
        for (int i = 1; i <= 2; i++) {
            board.addComment(writer[0], "이미지" + i, "댓글" + i);
        }
        for (int i = 3; i <= 5; i++) {
            board.addComment(writer[1], "이미지" + i, "댓글" + i);
        }

        assertAll(
                () -> assertThat(board.getCommentList()).hasSize(5),
                () -> assertThat(board.getCommentList())
                        .map(Comment::getImage)
                        .containsExactlyInAnyOrder("이미지1", "이미지2", "이미지3", "이미지4", "이미지5"),
                () -> assertThat(board.getCommentList())
                        .map(Comment::getContent)
                        .containsExactlyInAnyOrder("댓글1", "댓글2", "댓글3", "댓글4", "댓글5"),
                () -> assertThat(board.getCommentList())
                        .map(Comment::getWriter)
                        .containsExactlyInAnyOrder(writer[0], writer[0], writer[1], writer[1], writer[1])
        );
    }
}
