package umc.stockoneqback.board.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_0;
import static umc.stockoneqback.fixture.BoardFixture.BOARD_1;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Board [Repository Layer] -> BoardRepository 테스트")
public class BoardRepositoryTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = userRepository.save(SAEWOO.toUser());
        boardRepository.save(BOARD_0.toBoard(user));
        boardRepository.save(BOARD_1.toBoard(user));
    }

    @Test
    @DisplayName("회원 ID를 통해 해당 회원의 게시글을 모두 삭제한다")
    void deleteByUser() {
        // when
        boardRepository.deleteByWriter(user);

        // then
        assertThat(boardRepository.findAll().isEmpty()).isTrue();
    }
}
