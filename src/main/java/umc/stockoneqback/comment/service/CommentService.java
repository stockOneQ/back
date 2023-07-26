package umc.stockoneqback.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.service.BoardFindService;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.domain.CommentRepository;
import umc.stockoneqback.comment.exception.CommentErrorCode;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final UserFindService userFindService;
    private final BoardFindService boardFindService;
    private final CommentFindService commentFindService;
    private final CommentRepository commentRepository;

    @Transactional
    public Long create(Long writerId, Long boardId, String image, String content) {
        User writer = userFindService.findById(writerId);
        Board board = boardFindService.findById(boardId);
        Comment comment = Comment.createComment(writer, board, image, content);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void update(Long writerId, Long commentId, String image, String content) {
        validateWriter(commentId, writerId);
        Comment comment = commentFindService.findById(commentId);

        comment.updateImage(image);
        comment.updateContent(content);
    }

    @Transactional
    public void delete(Long writerId, Long commentId) {
        validateWriter(commentId, writerId);
        commentRepository.deleteById(commentId);
    }

    private void validateWriter(Long commentId, Long writerId) {
        Comment comment = commentFindService.findById(commentId);
        if (!comment.getWriter().getId().equals(writerId)) {
            throw BaseException.type(CommentErrorCode.USER_IS_NOT_COMMENT_WRITER);
        }
    }
}
