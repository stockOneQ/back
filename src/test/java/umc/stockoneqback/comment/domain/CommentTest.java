package umc.stockoneqback.comment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.COMMENT_0;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.base.Status.NORMAL;

@DisplayName("Comment 도메인 테스트")
public class CommentTest {
    private User writer;
    private Board board;
    private Comment comment;

    @BeforeEach
    void setUp() {
        writer = SAEWOO.toUser();
        board = BOARD_0.toBoard(writer);
        comment = COMMENT_0.toComment(writer, board);

    }

    @Test
    @DisplayName("Comment 생성에 성공한다")
    void success() {
        assertAll(
                () -> assertThat(comment.getWriter()).isEqualTo(writer),
                () -> assertThat(comment.getBoard()).isEqualTo(board),
                () -> assertThat(comment.getImage()).isEqualTo(COMMENT_0.getImage()),
                () -> assertThat(comment.getContent()).isEqualTo(COMMENT_0.getContent()),
                () -> assertThat(comment.getStatus()).isEqualTo(NORMAL)
        );
    }

    @Test
    @DisplayName("Comment에 Reply를 추가한다")
    void addChildComment() {
        for (int i = 1; i <= 5; i++) {
            comment.addReply(writer, comment, "이미지" + i, "대댓글" + i);
        }

        assertAll(
                () -> assertThat(comment.getReplyList()).hasSize(5),
                () -> assertThat(comment.getReplyList())
                        .map(Reply::getImage)
                        .containsExactlyInAnyOrder("이미지1", "이미지2", "이미지3", "이미지4", "이미지5"),
                () -> assertThat(comment.getReplyList())
                        .map(Reply::getContent)
                        .containsExactlyInAnyOrder("대댓글1", "대댓글2", "대댓글3", "대댓글4", "대댓글5"),
                () -> assertThat(comment.getReplyList())
                        .map(Reply::getComment)
                        .containsExactlyInAnyOrder(comment, comment, comment, comment, comment)
        );
    }
}
