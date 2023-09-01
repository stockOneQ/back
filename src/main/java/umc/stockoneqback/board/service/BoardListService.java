package umc.stockoneqback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.controller.dto.CustomBoardListResponse;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.domain.BoardSearchType;
import umc.stockoneqback.board.domain.BoardSortCondition;
import umc.stockoneqback.board.domain.like.BoardLikeRepository;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.board.infra.query.dto.BoardList;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.domain.CommentRepository;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.domain.ReplyRepository;
import umc.stockoneqback.user.domain.Role;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;

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

    @Transactional
    public CustomBoardListResponse<BoardList> getBoardList(Long userId, int page, String sortBy,
                                                           String searchBy, String searchWord) {
        BoardSortCondition boardSortCondition = BoardSortCondition.from(sortBy);
        BoardSearchType boardSearchType = BoardSearchType.from(searchBy);

        CustomBoardListResponse<BoardList> boardList = new CustomBoardListResponse<>();
        switch (boardSortCondition) {
            case TIME -> boardList = boardRepository.getBoardListOrderByTime(boardSearchType, searchWord, page);
            case HIT -> boardList = boardRepository.getBoardListOrderByHit(boardSearchType, searchWord, page);
        }

        List<BoardList> boardLists = getSortedBoardList(boardList);
        return new CustomBoardListResponse<>(boardList.getPageInfo(), boardLists);
    }

    @Transactional
    public CustomBoardListResponse<BoardList> getMyBoardList(Long userId, int page, String sortBy,
                                                             String searchBy, String searchWord) {
        User user = userFindService.findById(userId);
        validateUser(user);

        BoardSortCondition boardSortCondition = BoardSortCondition.from(sortBy);
        BoardSearchType boardSearchType = BoardSearchType.from(searchBy);

        CustomBoardListResponse<BoardList> boardList = new CustomBoardListResponse<>();
        switch (boardSortCondition) {
            case TIME -> boardList = boardRepository.getMyBoardListOrderByTime(userId, boardSearchType, searchWord, page);
            case HIT -> boardList = boardRepository.getMyBoardListOrderByHit(userId, boardSearchType, searchWord, page);
        }

        List<BoardList> boardLists = getSortedBoardList(boardList);
        return new CustomBoardListResponse<>(boardList.getPageInfo(), boardLists);
    }

    @Transactional
    public void deleteMyBoard(Long userId, List<Long> selectedBoardId) {
        for (Long boardId : selectedBoardId) {
            validateWriter(boardId, userId);
            boardRepository.deleteById(boardId);
        }
    }

    private List<BoardList> getSortedBoardList(CustomBoardListResponse<BoardList> boardLists) {
        List<BoardList> boardList1 = boardLists.getBoardList();
        List<BoardList> boardListResponseList = new ArrayList<>();

        for (BoardList boardList : boardList1) {
            Board board = boardFindService.findById(boardList.getId());
            BoardList boardListResponse = BoardList.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(checkContentLength(board))
                    .hit(board.getHit())
                    .createdDate(board.getCreatedDate())
                    .comment(countCommentAndReply(board))
                    .likes(countLike(board))
                    .build();
            boardListResponseList.add(boardListResponse);
        }
        return boardListResponseList;
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
