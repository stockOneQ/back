package umc.stockoneqback.reply.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.COMMENT_0;
import static umc.stockoneqback.fixture.ReplyFixture.REPLY_0;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;
import static umc.stockoneqback.global.base.Status.NORMAL;

@DisplayName("Reply 도메인 테스트")
public class ReplyTest {
    @Test
    @DisplayName("Reply 생성에 성공한다")
    void success() {
        User writer = SAEWOO.toUser();
        Board board = BOARD_0.toBoard(writer);
        Comment comment = COMMENT_0.toComment(writer, board);
        Reply reply = REPLY_0.toReply(writer, comment);

        assertAll(
                () -> assertThat(reply.getWriter()).isEqualTo(writer),
                () -> assertThat(reply.getComment()).isEqualTo(comment),
                () -> assertThat(reply.getImage()).isEqualTo(REPLY_0.getImage()),
                () -> assertThat(reply.getContent()).isEqualTo(REPLY_0.getContent()),
                () -> assertThat(reply.getStatus()).isEqualTo(NORMAL)
        );
    }
}
