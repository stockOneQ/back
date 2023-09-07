package umc.stockoneqback.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.exception.CommentErrorCode;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.service.ReplyService;
import umc.stockoneqback.user.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.*;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Comment [Service Layer] -> CommentService 테스트")
public class CommentServiceTest extends ServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentFindService commentFindService;

    @Autowired
    private ReplyService replyService;

    private User writer;
    private User not_writer;

    private Board board;

    private final Comment[] comments = new Comment[3];

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setup() {
        writer = userRepository.save(SAEWOO.toUser());
        not_writer = userRepository.save(ANNE.toUser());

        board = boardRepository.save(BOARD_0.toBoard(writer));

        comments[0] = commentRepository.save(COMMENT_0.toComment(writer, board));
        comments[1] = commentRepository.save(COMMENT_1.toComment(writer, board));
        comments[2] = commentRepository.save(COMMENT_2.toComment(writer, board));
    }

    @Nested
    @DisplayName("댓글 등록")
    class createComment {
        @Test
        @DisplayName("댓글 등록에 성공한다")
        void success() {
            // when - then
            Comment findComment = commentRepository.findById(comments[0].getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findComment.getWriter().getId()).isEqualTo(writer.getId()),
                    () -> assertThat(findComment.getBoard().getId()).isEqualTo(board.getId()),
                    () -> assertThat(findComment.getImage()).isEqualTo(COMMENT_0.getImage()),
                    () -> assertThat(findComment.getContent()).isEqualTo(COMMENT_0.getContent()),
                    () -> assertThat(findComment.getCreatedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter)),
                    () -> assertThat(findComment.getModifiedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter))
            );
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class update {
        @Test
        @DisplayName("다른 사람의 댓글은 수정할 수 없다")
        void throwExceptionByUserNotCommentWriter() {
            // when - then
            assertThatThrownBy(() -> commentService.update(not_writer.getId(),board.getId(), null, "내용2"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(CommentErrorCode.USER_IS_NOT_COMMENT_WRITER.getMessage());
        }

        @Test
        @DisplayName("댓글 수정에 성공한다")
        void success() {
            // given
            commentService.update(writer.getId(), board.getId(), null,"내용2");

            // when
            Comment findComment = commentFindService.findById(comments[0].getId());

            // then
            assertAll(
                    () -> assertThat(findComment.getImage()).isEqualTo(null),
                    () -> assertThat(findComment.getContent()).isEqualTo("내용2"),
                    () -> assertThat(findComment.getModifiedDate().format(formatter)).isEqualTo(LocalDateTime.now().format(formatter))
            );
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class delete {
        @Test
        @DisplayName("다른 사람의 댓글은 삭제할 수 없다")
        void throwExceptionByUserIsNotCommentWriter() {
            // when - then
            assertThatThrownBy(() -> commentService.delete(not_writer.getId(), comments[0].getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(CommentErrorCode.USER_IS_NOT_COMMENT_WRITER.getMessage());
        }

        @Test
        @DisplayName("게시글의 특정 댓글을 삭제할 수 있다")
        void successDeleteSpecificComment() {
            // when
            commentService.delete(writer.getId(), comments[0].getId());
            commentService.delete(writer.getId(), comments[1].getId());

            // then
            assertAll(
                    () -> assertThat(commentRepository.countByBoard(board)).isEqualTo(1L),
                    () -> assertThat(commentRepository.existsById(comments[0].getId())).isFalse(),
                    () -> assertThat(commentRepository.existsById(comments[1].getId())).isFalse()
            );
        }

        @Test
        @DisplayName("댓글이 삭제되면 달린 대댓글도 삭제되어야 한다")
        void successDeleteAllReply() {
            // given
            for(int i=1; i<=5; i++) {
                replyService.create(writer.getId(), comments[0].getId(), null, "댓글" + i);
            }
            flushAndClear();

            // when
            commentService.delete(writer.getId(), board.getId());

            // then
            assertThat(replyRepository.count()).isEqualTo(0);
        }

        @Test
        @DisplayName("댓글 삭제에 성공한다")
        void success() {
            // when
            commentService.delete(writer.getId(), comments[0].getId());

            // then
            assertThatThrownBy(() -> commentFindService.findById(comments[0].getId()))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(CommentErrorCode.COMMENT_NOT_FOUND.getMessage());
        }
    }

    @Test
    @DisplayName("현재 사용자의 댓글 전체 삭제에 성공한다")
    void deleteAll() {
        // when
        commentService.deleteByWriter(writer);

        // then
        assertAll(
                () -> assertThat(commentRepository.findById(comments[0].getId()).isEmpty()).isTrue(),
                () -> assertThat(commentRepository.findById(comments[1].getId()).isEmpty()).isTrue(),
                () -> assertThat(commentRepository.findById(comments[2].getId()).isEmpty()).isTrue()
        );
    }
}

