package umc.stockoneqback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.controller.dto.BoardListResponse;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.domain.SearchType;
import umc.stockoneqback.board.domain.SortCondition;
import umc.stockoneqback.board.domain.like.BoardLikeRepository;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.domain.CommentRepository;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.reply.domain.ReplyRepository;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardListService {
    private final BoardRepository boardRepository;
    private final BoardFindService boardFindService;
    private final BoardLikeRepository boardLikeRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserFindService userFindService;

    private static final Integer PAGE_SIZE = 7;

    @Transactional
    public BoardListResponse getBoardList(Long userId, Long lastBoardId, String sortBy, String searchBy, String searchWord) throws IOException {
        User user = userFindService.findById(userId);
        validateManager(user);

        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        SearchType searchType = SearchType.findSearchTypeByValue(searchBy);

        List<BoardList> boardList = new ArrayList<>();
        switch (sortCondition) {
            case TIME -> boardList = boardRepository.getBoardListOrderByTime(searchType, searchWord);
            case HIT -> boardList = boardRepository.getBoardListOrderByHit(searchType, searchWord);
        }

        List<BoardList> boardLists = getSortedBoardList(boardList);

        int lastIndex = getLastIndex(boardLists, lastBoardId);
        return configPaging(boardLists, lastIndex, PAGE_SIZE);
    }

    @Transactional
    public BoardListResponse getMyBoardList(Long userId, Long lastBoardId, String sortBy, String searchBy, String searchWord) throws IOException {
        User user = userFindService.findById(userId);
        validateUser(user);

        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);
        SearchType searchType = SearchType.findMyBoardSearchTypeByValue(searchBy);

        List<BoardList> boardList = new ArrayList<>();
        switch (sortCondition) {
            case TIME -> boardList = boardRepository.getMyBoardListOrderByTime(userId, searchType, searchWord);
            case HIT -> boardList = boardRepository.getMyBoardListOrderByHit(userId, searchType, searchWord);
        }

        List<BoardList> boardLists = getSortedBoardList(boardList);

        int lastIndex = getLastIndex(boardLists, lastBoardId);
        return configPaging(boardLists, lastIndex, PAGE_SIZE);
    }

    @Transactional
    public void deleteMyBoard(Long userId, List<Long> selectedBoardId) {
        for (Long boardId : selectedBoardId) {
            validateWriter(boardId, userId);
            boardRepository.deleteById(boardId);
        }
    }

    private List<BoardList> getSortedBoardList(List<BoardList> boardLists) throws IOException {
        List<BoardList> boardListResponseList = new ArrayList<>();

        for (BoardList boardList : boardLists) {
            Board board = boardFindService.findById(boardList.getId());
            BoardList boardListResponse = BoardList.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(checkContentLength(board))
                    .hit(board.getHit())
                    .createdDate(board.getCreatedDate())
                    .comment(countCommentAndReply(board))
                    .like(countLike(board))
                    .build();
            boardListResponseList.add(boardListResponse);
        }
        return boardListResponseList;
    }

    private int getLastIndex(List<BoardList> boardLists, Long lastBoardId) {
        return boardLists.indexOf(
                boardLists.stream()
                        .filter(boardList -> boardList.getId().equals(lastBoardId))
                        .findFirst()
                        .orElse(null)
        );
    }

    private BoardListResponse configPaging(List<BoardList> boardLists, int lastIndex, int size) {
        if (lastIndex + 1 + size >= boardLists.size()) {
            return new BoardListResponse(boardLists.subList(lastIndex + 1, boardLists.size()));
        }
        return new BoardListResponse(boardLists.subList(lastIndex + 1, lastIndex + 1 + size));
    }

    private int countLike(Board board) {
        return boardLikeRepository.countByBoard(board);
    }

    private int countCommentAndReply(Board board) {
        int commentNum = commentRepository.countByBoard(board);
        List<Comment> commentList = commentRepository.findAllByBoard(board);

        for (Comment comment : commentList) {
            int replyNum = replyRepository.countByComment(comment);
            commentNum += replyNum;
        }
        return commentNum;
    }

    private String checkContentLength(Board board) {
        String content = board.getContent();
        if (content.length() > 101) {
            String contentPreview = content.substring(0, 100);
            content = contentPreview.concat("...");
        }
        return content;
    }

    private void validateManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw BaseException.type(UserErrorCode.USER_IS_NOT_MANAGER);
        }
    }

    private void validateWriter(Long boardId, Long writerId) {
        Board board = boardFindService.findById(boardId);
        if (!board.getWriter().getId().equals(writerId)) {
            throw BaseException.type(BoardErrorCode.USER_IS_NOT_BOARD_WRITER);
        }
    }

    private void validateUser(User user) {
        if (user.getRole() != Role.MANAGER || user.getStatus() == Status.EXPIRED) {
            throw BaseException.type(UserErrorCode.USER_NOT_ALLOWED);
        }
    }
}
