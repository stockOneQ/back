package umc.stockoneqback.board.infra.query;

import umc.stockoneqback.board.infra.query.dto.BoardList;

import java.util.List;

public interface BoardListQueryRepository {
    public List<BoardList> getBoardListOrderByTime();
    public List<BoardList> getBoardListOrderByHit();
}
