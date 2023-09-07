package umc.stockoneqback.reply.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.domain.CommentRepository;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.COMMENT_0;
import static umc.stockoneqback.fixture.ReplyFixture.*;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Reply [Repository Layer] -> ReplyRepository 테스트")
public class ReplyRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReplyRepository replyRepository;

    private User writer;

    private Comment comment;
    private final Reply[] replies = new Reply[3];

    @BeforeEach
    void setup() {
        writer = userRepository.save(SAEWOO.toUser());

        Board board = boardRepository.save(BOARD_0.toBoard(writer));
        comment = commentRepository.save(COMMENT_0.toComment(writer, board));
        replies[0] = replyRepository.save(REPLY_0.toReply(writer, comment));
        replies[1] = replyRepository.save(REPLY_1.toReply(writer, comment));
        replies[2] = replyRepository.save(REPLY_2.toReply(writer, comment));
    }

    @Test
    @DisplayName("댓글의 대댓글 수를 가져온다")
    void countByBoard() {
        assertThat(replyRepository.countByComment(comment)).isEqualTo(3L);
    }

    @Test
    @DisplayName("회원 ID를 통해 해당 회원의 대댓글을 모두 삭제한다")
    void deleteByUser() {
        // when
        replyRepository.deleteByWriter(writer);

        // then
        assertThat(replyRepository.findAll().isEmpty()).isTrue();
    }
}


