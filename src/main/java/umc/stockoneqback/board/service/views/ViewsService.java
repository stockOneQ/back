package umc.stockoneqback.board.service.views;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.domain.views.Views;
import umc.stockoneqback.board.domain.views.ViewsRedisRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ViewsService {
    private final ViewsRedisRepository viewsRedisRepository;

    @Transactional
    public Views saveViews(Long userId) {
        return viewsRedisRepository.save(Views.createViews(userId));
    }

    @Transactional
    public void updateViews(Views views, Long boardId) {
        views.updateBoardIdList(boardId);
        viewsRedisRepository.save(views);
    }

    public Optional<Views> findById(Long userId) {
        return viewsRedisRepository.findById(userId);
    }

    public boolean existBoardIdByUserId(Long userId, Long boardId) {
        return viewsRedisRepository.findById(userId)
                .map(views -> views.getBoardIdList().contains(boardId))
                .orElse(false);
    }
}
