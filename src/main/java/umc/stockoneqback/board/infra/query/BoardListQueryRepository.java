package umc.stockoneqback.board.infra.query;

import umc.stockoneqback.board.domain.SearchType;
import umc.stockoneqback.board.infra.query.dto.BoardList;

import java.util.List;

public interface BoardListQueryRepository {
    public List<BoardList> getBoardListOrderByTime(SearchType searchType, String searchWord);
    public List<BoardList> getBoardListOrderByHit(SearchType searchType, String searchWord);
    public List<BoardList> getMyBoardListOrderByTime(Long userId, SearchType searchType, String searchWord);
    public List<BoardList> getMyBoardListOrderByHit(Long userId, SearchType searchType, String searchWord);
}
