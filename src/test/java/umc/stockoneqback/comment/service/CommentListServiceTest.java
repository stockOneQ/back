package umc.stockoneqback.comment.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.controller.dto.CustomCommentListResponse;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.user.domain.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.*;
import static umc.stockoneqback.fixture.ReplyFixture.*;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@DisplayName("Comment [Service Layer] -> CommentListService 테스트")
class CommentListServiceTest extends ServiceTest {
    @Autowired
    private CommentListService commentListService;

    private final Comment[] commentList = new Comment[15];
    private final Reply[] replyList = new Reply[10];

    private User writer;
    private Board board;

    private static final int PAGE = 0;
    private static final int PAGE_SIZE = 20; // 대댓글 포함 20개

    @BeforeEach
    void setUp() {
        writer = userRepository.save(ANNE.toUser());
        board = boardRepository.save(BOARD_0.toBoard(writer));

        commentList[0] = commentRepository.save(COMMENT_0.toComment(writer, board));
        commentList[1] = commentRepository.save(COMMENT_1.toComment(writer, board));
        commentList[2] = commentRepository.save(COMMENT_2.toComment(writer, board));
        commentList[3] = commentRepository.save(COMMENT_3.toComment(writer, board));
        commentList[4] = commentRepository.save(COMMENT_4.toComment(writer, board));
        commentList[5] = commentRepository.save(COMMENT_5.toComment(writer, board));
        commentList[6] = commentRepository.save(COMMENT_6.toComment(writer, board));
        commentList[7] = commentRepository.save(COMMENT_7.toComment(writer, board));
        commentList[8] = commentRepository.save(COMMENT_8.toComment(writer, board));
        commentList[9] = commentRepository.save(COMMENT_9.toComment(writer, board));
        commentList[10] = commentRepository.save(COMMENT_10.toComment(writer, board));
        commentList[11] = commentRepository.save(COMMENT_11.toComment(writer, board));
        commentList[12] = commentRepository.save(COMMENT_12.toComment(writer, board));
        commentList[13] = commentRepository.save(COMMENT_13.toComment(writer, board));
        commentList[14] = commentRepository.save(COMMENT_14.toComment(writer, board));

        replyList[0] = replyRepository.save(REPLY_0.toReply(writer, commentList[0]));
        replyList[1] = replyRepository.save(REPLY_1.toReply(writer, commentList[0]));
        replyList[2] = replyRepository.save(REPLY_2.toReply(writer, commentList[0]));
        replyList[3] = replyRepository.save(REPLY_3.toReply(writer, commentList[0]));
        replyList[4] = replyRepository.save(REPLY_4.toReply(writer, commentList[0]));
        replyList[5] = replyRepository.save(REPLY_5.toReply(writer, commentList[0]));
        replyList[6] = replyRepository.save(REPLY_6.toReply(writer, commentList[0]));
        replyList[7] = replyRepository.save(REPLY_7.toReply(writer, commentList[0]));
        replyList[8] = replyRepository.save(REPLY_8.toReply(writer, commentList[0]));
        replyList[9] = replyRepository.save(REPLY_9.toReply(writer, commentList[0]));
    }

    @AfterEach
    void clearAll() {
        boardRepository.deleteAll();
    }

    @Nested
    @DisplayName("전체 댓글 조회")
    class allCommentList {
        @Test
        @DisplayName("댓글 목록 조회에 성공한다")
        void success() throws IOException {
            // when
            CustomCommentListResponse customCommentListResponse = commentListService.getCommentList(writer.getId(), board.getId(), PAGE);

            // then
            assertThat(customCommentListResponse.CommentListResponse().size()).isLessThanOrEqualTo(PAGE_SIZE);

            assertAll(
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).id()).isEqualTo(commentList[0].getId()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).image()).isEqualTo(commentList[0].getImage()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).content()).isEqualTo(commentList[0].getContent()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).createdDate()).isEqualTo(commentList[0].getCreatedDate()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).writerId()).isEqualTo(commentList[0].getWriter().getLoginId()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).writerName()).isEqualTo(commentList[0].getWriter().getName()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).replyList().get(0).id()).isEqualTo(replyList[0].getId()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).replyList().get(0).image()).isEqualTo(replyList[0].getImage()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).replyList().get(0).content()).isEqualTo(replyList[0].getContent()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).replyList().get(0).createdDate()).isEqualTo(replyList[0].getCreatedDate()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).replyList().get(0).writerId()).isEqualTo(replyList[0].getWriter().getLoginId()),
                    () -> assertThat(customCommentListResponse.CommentListResponse().get(0).replyList().get(0).writerName()).isEqualTo(replyList[0].getWriter().getName())
            );
        }
    }
}
