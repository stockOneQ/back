package umc.stockoneqback.reply.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.reply.controller.dto.ReplyListResponse;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.user.domain.User;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.COMMENT_0;
import static umc.stockoneqback.fixture.ReplyFixture.*;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@DisplayName("Reply [Service Layer] -> ReplyListService 테스트")
class ReplyListServiceTest extends ServiceTest {
    @Autowired
    private ReplyListService replyListService;

    private final Reply[] replyList = new Reply[10];
    private User writer;
    private Board board;
    private Comment comment;

    @BeforeEach
    void setUp() {
        boardRepository.deleteAll();
        writer = userRepository.save(ANNE.toUser());
        board = boardRepository.save(BOARD_0.toBoard(writer));
        comment = commentRepository.save(COMMENT_0.toComment(writer, board));

        replyList[0] = replyRepository.save(REPLY_0.toReply(writer, comment));
        replyList[1] = replyRepository.save(REPLY_1.toReply(writer, comment));
        replyList[2] = replyRepository.save(REPLY_2.toReply(writer, comment));
        replyList[3] = replyRepository.save(REPLY_3.toReply(writer, comment));
        replyList[4] = replyRepository.save(REPLY_4.toReply(writer, comment));
        replyList[5] = replyRepository.save(REPLY_5.toReply(writer, comment));
        replyList[6] = replyRepository.save(REPLY_6.toReply(writer, comment));
        replyList[7] = replyRepository.save(REPLY_7.toReply(writer, comment));
        replyList[8] = replyRepository.save(REPLY_8.toReply(writer, comment));
        replyList[9] = replyRepository.save(REPLY_9.toReply(writer, comment));


    }

    @Nested
    @DisplayName("전체 대댓글 조회")
    class getReplyList {
        @Test
        @DisplayName("대댓글 목록 조회(작성순)에 성공한다")
        void success() throws IOException {
            // when
            List<ReplyListResponse> replyListResponse = replyListService.getReplyList(writer.getId(), comment.getId());

            // then
            assertAll(
                    () -> assertThat(replyListResponse.get(0).id()).isEqualTo(replyList[0].getId()),
                    () -> assertThat(replyListResponse.get(0).image()).isEqualTo(replyList[0].getImage()),
                    () -> assertThat(replyListResponse.get(0).content()).isEqualTo(replyList[0].getContent()),
                    () -> assertThat(replyListResponse.get(0).createdDate()).isEqualTo(replyList[0].getCreatedDate()),
                    () -> assertThat(replyListResponse.get(0).writerId()).isEqualTo(replyList[0].getWriter().getLoginId()),
                    () -> assertThat(replyListResponse.get(0).writerName()).isEqualTo(replyList[0].getWriter().getName())
            );
        }
    }
}

