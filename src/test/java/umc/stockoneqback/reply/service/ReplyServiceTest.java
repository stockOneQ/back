package umc.stockoneqback.reply.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.reply.exception.ReplyErrorCode;
import umc.stockoneqback.user.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.COMMENT_0;
import static umc.stockoneqback.fixture.ReplyFixture.*;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Reply [Service Layer] -> ReplyService 테스트")
public class ReplyServiceTest extends ServiceTest {
    @Autowired
    private ReplyService replyService;

    @Autowired
    private ReplyFindService replyFindService;

    private User writer;
    private User not_writer;

    private Board board;

    private Comment comment;
    private final Reply[] replies = new Reply[3];

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setup() {
        writer = userRepository.save(SAEWOO.toUser());
        not_writer = userRepository.save(ANNE.toUser());

        board = boardRepository.save(BOARD_0.toBoard(writer));

        comment = commentRepository.save(COMMENT_0.toComment(writer, board));
        replies[0] = replyRepository.save(REPLY_0.toReply(writer, comment));
        replies[1] = replyRepository.save(REPLY_1.toReply(writer, comment));
        replies[2] = replyRepository.save(REPLY_2.toReply(writer, comment));
    }

    @Nested
    @DisplayName("대댓글 등록")
    class createComment {
        @Test
        @DisplayName("대댓글 등록에 성공한다")
        void success() {
            // when - then
            Reply reply = replyRepository.findById(replies[0].getId()).orElseThrow();
            assertAll(
                    () -> assertThat(reply.getWriter().getId()).isEqualTo(writer.getId()),
                    () -> assertThat(reply.getComment().getId()).isEqualTo(comment.getId()),
                    () -> assertThat(reply.getImage()).isEqualTo(REPLY_0.getImage()),
                    () -> assertThat(reply.getContent()).isEqualTo(REPLY_0.getContent()),
                    () -> assertThat(reply.getCreatedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter)),
                    () -> assertThat(reply.getModifiedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter))
            );
        }
    }

    @Nested
    @DisplayName("대댓글 수정")
    class update {
        @Test
        @DisplayName("다른 사람의 대댓글은 수정할 수 없다")
        void throwExceptionByUserNotReplyWriter() {
            // when - then
            assertThatThrownBy(() -> replyService.update(not_writer.getId(),comment.getId(), null, "내용2"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER.getMessage());
        }

        @Test
        @DisplayName("대댓글 수정에 성공한다")
        void success() {
            // given
            replyService.update(writer.getId(), board.getId(), null,"내용2");

            // when
            Reply reply = replyFindService.findById(replies[0].getId());

            // then
            assertAll(
                    () -> assertThat(reply.getImage()).isEqualTo(null),
                    () -> assertThat(reply.getContent()).isEqualTo("내용2"),
                    () -> assertThat(reply.getModifiedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter))
            );
        }
    }

    @Nested
    @DisplayName("대댓글 삭제")
    class delete {
        @Test
        @DisplayName("다른 사람의 대댓글은 삭제할 수 없다")
        void throwExceptionByUserIsNotReplyWriter() {
            // when - then
            assertThatThrownBy(() -> replyService.delete(not_writer.getId(), replies[0].getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER.getMessage());
        }

        @Test
        @DisplayName("댓글의 특정 대댓글을 삭제할 수 있다")
        void successDeleteSpecificReply() {
            // when
            replyService.delete(writer.getId(), replies[0].getId());
            replyService.delete(writer.getId(), replies[1].getId());

            // then
            assertAll(
                    () -> assertThat(replyRepository.countByComment(comment)).isEqualTo(1L),
                    () -> assertThat(replyRepository.existsById(replies[0].getId())).isFalse(),
                    () -> assertThat(replyRepository.existsById(replies[1].getId())).isFalse()
            );
        }

        @Test
        @DisplayName("대댓글 삭제에 성공한다")
        void success() {
            // when
            replyService.delete(writer.getId(), replies[0].getId());

            // then
            assertThatThrownBy(() -> replyFindService.findById(replies[0].getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(ReplyErrorCode.REPLY_NOT_FOUND.getMessage());
        }
    }

    @Test
    @DisplayName("현재 사용자의 대댓글 전체 삭제에 성공한다")
    void deleteAll() {
        // when
        replyService.deleteByWriter(writer);

        // then
        assertAll(
                () -> assertThat(replyRepository.findById(replies[0].getId()).isEmpty()).isTrue(),
                () -> assertThat(replyRepository.findById(replies[1].getId()).isEmpty()).isTrue(),
                () -> assertThat(replyRepository.findById(replies[2].getId()).isEmpty()).isTrue()
        );
    }
}

