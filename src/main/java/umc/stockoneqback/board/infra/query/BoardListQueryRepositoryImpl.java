package umc.stockoneqback.board.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.domain.SearchType;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.board.infra.query.dto.QBoardList;
import umc.stockoneqback.global.base.Status;

import java.util.List;

import static umc.stockoneqback.board.domain.QBoard.board;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardListQueryRepositoryImpl implements BoardListQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<BoardList> getBoardListOrderByTime(SearchType searchType, String searchWord) {
        return query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), search(searchType, searchWord))
                .orderBy(board.createdDate.desc())
                .fetch();
    }

    @Override
    public List<BoardList> getBoardListOrderByHit(SearchType searchType, String searchWord) {
        return query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), search(searchType, searchWord))
                .orderBy(board.hit.desc())
                .fetch();
    }

    @Override
    public List<BoardList> getMyBoardListOrderByTime(Long userId, SearchType searchType, String searchWord) {
        return query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), board.writer.id.eq(userId), search(searchType, searchWord))
                .orderBy(board.createdDate.desc())
                .fetch();
    }

    @Override
    public List<BoardList> getMyBoardListOrderByHit(Long userId, SearchType searchType, String searchWord) {
        return query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), board.writer.id.eq(userId), search(searchType, searchWord))
                .orderBy(board.hit.desc())
                .fetch();
    }

    private BooleanExpression search(SearchType searchType, String searchWord) {
        if (searchWord == null || searchWord.isEmpty()) {
            return null;
        } else {
            switch (searchType) {
                case WRITER -> {
                    return board.writer.name.contains(searchWord);
                }
                case TITLE -> {
                    return board.title.contains(searchWord);
                }
                case CONTENT -> {
                    return board.content.contains(searchWord);
                }
                default -> {
                    return null;
                }
            }
        }
    }
}
