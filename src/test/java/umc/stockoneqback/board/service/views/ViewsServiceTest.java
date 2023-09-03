package umc.stockoneqback.board.service.views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.board.domain.views.Views;
import umc.stockoneqback.common.ServiceTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Views [Service Layer] -> ViewsService 테스트")
class ViewsServiceTest extends ServiceTest {
    @Autowired
    private ViewsService viewsService;

    @Test
    @DisplayName("Views를 저장한다")
    void saveViews() {
        // when
        viewsService.saveViews(1L);

        // then
        Views findViews = viewsRedisRepository.findById(1L).orElseThrow();
        assertAll(
                () -> assertThat(findViews.getId()).isEqualTo(1L),
                () -> assertThat(findViews.getBoardIdList().size()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("Views의 BoardIdList에 boardId를 추가한다")
    void updateViews() {
        // given
        Views views = viewsRedisRepository.save(Views.createViews(1L));

        // when
        viewsService.updateViews(views, 1L);

        // then
        Views findViews = viewsRedisRepository.findById(1L).orElseThrow();
        assertAll(
                () -> assertThat(findViews.getId()).isEqualTo(1L),
                () -> assertThat(findViews.getBoardIdList().size()).isEqualTo(1),
                () -> assertThat(findViews.getBoardIdList()).contains(1L)
        );
    }

    @Test
    @DisplayName("ID (PK)를 이용하여 Views를 조회한다")
    void findById() {
        // given
        Long viewsId = viewsRedisRepository.save(Views.createViews(1L)).getId();

        // when
        Optional<Views> viewsExist = viewsService.findById(viewsId);
        Optional<Views> viewsNotExist = viewsService.findById(viewsId + 100L);

        // then
        assertAll(
                () -> assertThat(viewsExist.get().getId()).isEqualTo(viewsId),
                () -> assertThat(viewsExist.get().getBoardIdList().size()).isEqualTo(0),
                () -> assertThat(viewsExist.get().getExpiration()).isEqualTo(Views.DEFAULT_TTL),
                () -> assertThat(viewsNotExist.isEmpty()).isTrue()
        );
    }

    @Test
    @DisplayName("사용자 ID (PK)와 게시글 ID (PK)로 BoardIdList에 게시글 ID가 있는지 검증한다")
    void existBoardIdByUserId() {
        // given
        Views views = viewsRedisRepository.save(Views.createViews(1L));
        viewsService.updateViews(views, 1L);

        // when - then
        assertThat(viewsService.existBoardIdByUserId(views.getId(), 1L)).isTrue();
    }
}