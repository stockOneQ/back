package umc.stockoneqback.board.infra.query;

import umc.stockoneqback.board.controller.dto.CustomBoardListResponse;
import umc.stockoneqback.board.domain.SearchType;
import umc.stockoneqback.board.infra.query.dto.BoardList;

public interface BoardListQueryRepository {
    public CustomBoardListResponse<BoardList> getBoardListOrderByTime(SearchType searchType, String searchWord, int page);
    public CustomBoardListResponse<BoardList> getBoardListOrderByHit(SearchType searchType, String searchWord, int page);
    public CustomBoardListResponse<BoardList> getMyBoardListOrderByTime(Long userId, SearchType searchType, String searchWord, int page);
    public CustomBoardListResponse<BoardList> getMyBoardListOrderByHit(Long userId, SearchType searchType, String searchWord, int page);
}
