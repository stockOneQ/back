package umc.stockoneqback.board.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.controller.dto.CustomBoardListResponse;
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
    public CustomBoardListResponse<BoardList> getBoardListOrderByTime(SearchType searchType, String searchWord, int page) {
        Pageable pageable = PageRequest.of(page, 7);
        List<BoardList> boardLists = query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), search(searchType, searchWord))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(board.countDistinct())
                .from(board)
                .where(board.status.eq(Status.NORMAL), search(searchType, searchWord))
                .orderBy(board.createdDate.desc());

        return new CustomBoardListResponse<>(PageableExecutionUtils.getPage(boardLists, pageable, countQuery::fetchOne));
    }

    @Override
    public CustomBoardListResponse<BoardList> getBoardListOrderByHit(SearchType searchType, String searchWord, int page) {
        Pageable pageable = PageRequest.of(page, 7);
        List<BoardList> boardLists = query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), search(searchType, searchWord))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.hit.desc())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(board.countDistinct())
                .from(board)
                .where(board.status.eq(Status.NORMAL), search(searchType, searchWord))
                .orderBy(board.hit.desc());

        return new CustomBoardListResponse<>(PageableExecutionUtils.getPage(boardLists, pageable, countQuery::fetchOne));
    }

    @Override
    public CustomBoardListResponse<BoardList> getMyBoardListOrderByTime(Long userId, SearchType searchType, String searchWord, int page) {
        Pageable pageable = PageRequest.of(page, 7);
        List<BoardList> boardLists = query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), board.writer.id.eq(userId), search(searchType, searchWord))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.createdDate.desc())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(board.countDistinct())
                .from(board)
                .where(board.status.eq(Status.NORMAL), board.writer.id.eq(userId), search(searchType, searchWord))
                .orderBy(board.createdDate.desc());

        return new CustomBoardListResponse<>(PageableExecutionUtils.getPage(boardLists, pageable, countQuery::fetchOne));
    }

    @Override
    public CustomBoardListResponse<BoardList> getMyBoardListOrderByHit(Long userId, SearchType searchType, String searchWord, int page) {
        Pageable pageable = PageRequest.of(page, 7);
        List<BoardList> boardLists = query
                .selectDistinct(new QBoardList(board.id, board.title, board.content, board.hit, board.createdDate))
                .from(board)
                .where(board.status.eq(Status.NORMAL), board.writer.id.eq(userId), search(searchType, searchWord))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.hit.desc())
                .fetch();

        JPAQuery<Long> countQuery = query
                .select(board.countDistinct())
                .from(board)
                .where(board.status.eq(Status.NORMAL), board.writer.id.eq(userId), search(searchType, searchWord))
                .orderBy(board.hit.desc());

        return new CustomBoardListResponse<>(PageableExecutionUtils.getPage(boardLists, pageable, countQuery::fetchOne));
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
