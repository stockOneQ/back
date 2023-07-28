package umc.stockoneqback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.controller.dto.BoardListResponse;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.domain.BoardRepository;
import umc.stockoneqback.board.domain.SortCondition;
import umc.stockoneqback.board.domain.like.BoardLikeRepository;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.domain.CommentRepository;
import umc.stockoneqback.global.base.BaseException;
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
    public List<BoardListResponse> getBoardList(Long userId, Long lastBoardId, String sortBy) throws IOException {
        User user = userFindService.findById(userId);
        validateManager(user);

        Board board = configPaging(lastBoardId);
        SortCondition sortCondition = SortCondition.findSortConditionByValue(sortBy);

        List<Board> boardList = new ArrayList<>();
        switch (sortCondition) {
            case TIME -> boardList = boardRepository.getBoardListOrderByTime(board.getId(), board.getCreatedDate(), PAGE_SIZE);
            case HIT -> boardList = boardRepository.getBoardListOrderByHit(board.getId(), board.getHit(), PAGE_SIZE);
        }
        return boardListResponse(boardList);
    }

    private Board configPaging(Long boardId) {
        if (boardId == null) return Board.builder().build();
        return boardFindService.findById(boardId);
    }

    private List<BoardListResponse> boardListResponse(List<Board> boardList) throws IOException {
        List<BoardListResponse> boardListResponseList = new ArrayList<>();
        for (Board board : boardList) {
            BoardListResponse boardListResponse = BoardListResponse.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(checkContentLength(board))
                    .hit(board.getHit())
                    .comment(countCommentAndReply(board))
                    .like(countLike(board))
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

    private void validateManager(User user) {
        if (user.getRole() != Role.MANAGER) {
            throw BaseException.type(UserErrorCode.USER_IS_NOT_MANAGER);
        }
    }
}
