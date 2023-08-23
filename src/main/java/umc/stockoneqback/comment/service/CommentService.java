package umc.stockoneqback.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.board.domain.Board;
import umc.stockoneqback.board.service.BoardFindService;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.domain.CommentRepository;
import umc.stockoneqback.comment.exception.CommentErrorCode;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.exception.BaseException;
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
    private final FileService fileService;

    @Transactional
    public Long create(Long writerId, Long boardId, MultipartFile image, String content) {
        User writer = userFindService.findById(writerId);
        Board board = boardFindService.findById(boardId);
        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadBoardFiles(image);

        Comment comment = Comment.createComment(writer, board, imageUrl, content);

        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void update(Long writerId, Long commentId, MultipartFile image, String content) {
        validateWriter(commentId, writerId);
        Comment comment = commentFindService.findById(commentId);
        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadBoardFiles(image);

        comment.updateImage(imageUrl);
        comment.updateContent(content);
    }

    @Transactional
    public void delete(Long writerId, Long commentId) {
        validateWriter(commentId, writerId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void deleteByWriter(User writer) {
        commentRepository.deleteByWriter(writer);
    }

    private void validateWriter(Long commentId, Long writerId) {
        Comment comment = commentFindService.findById(commentId);
        if (!comment.getWriter().getId().equals(writerId)) {
            throw BaseException.type(CommentErrorCode.USER_IS_NOT_COMMENT_WRITER);
        }
    }
}
