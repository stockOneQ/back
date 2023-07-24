package umc.stockoneqback.comment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.CommentFixture.*;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

public class CommentRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Board board;
    private final Comment[] comments = new Comment[3];

    @BeforeEach
    void setup() {
        User writer = userRepository.save(SAEWOO.toUser());
        board = boardRepository.save(BOARD_0.toBoard(writer));
        comments[0] = commentRepository.save(COMMENT_0.toComment(writer, board));
        comments[1] = commentRepository.save(COMMENT_1.toComment(writer, board));
        comments[2] = commentRepository.save(COMMENT_2.toComment(writer, board));
    }

    @Test
    void countByBoard() {
        assertThat(commentRepository.countByBoard(board)).isEqualTo(3L);
    }
}
