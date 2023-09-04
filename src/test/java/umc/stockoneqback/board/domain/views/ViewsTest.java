package umc.stockoneqback.board.domain.views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Views 도메인 테스트")
class ViewsTest {
    @Test
    @DisplayName("Views를 생성한다")
    void createViews() {
        Views views = Views.createViews(1L);

        assertAll(
                () -> assertThat(views.getId()).isEqualTo(1L),
                () -> assertThat(views.getBoardIdList()).isEmpty(),
                () -> assertThat(views.getExpiration()).isEqualTo(Views.DEFAULT_TTL)
        );
    }

    @Test
    @DisplayName("Views의 조회한 게시글 ID를 추가한다")
    void updateBoardIdList() {
        Views views = Views.createViews(1L);
        views.updateBoardIdList(1L);
        views.updateBoardIdList(2L);
        views.updateBoardIdList(3L);

        assertAll(
                () -> assertThat(views.getId()).isEqualTo(1L),
                () -> assertThat(views.getBoardIdList().size()).isEqualTo(3),
                () -> assertThat(views.getBoardIdList()).containsAll(List.of(1L, 2L, 3L)),
                () -> assertThat(views.getExpiration()).isEqualTo(Views.DEFAULT_TTL)
        );
    }
}