package umc.stockoneqback.reply.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.service.CommentFindService;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.reply.domain.ReplyRepository;
import umc.stockoneqback.reply.exception.ReplyErrorCode;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserFindService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyService {
    private final UserFindService userFindService;
    private final CommentFindService commentFindService;
    private final ReplyFindService replyFindService;
    private final ReplyRepository replyRepository;
    private final FileService fileService;

    @Transactional
    public Long create(Long writerId, Long commentId, MultipartFile image, String content) {
        User writer = userFindService.findById(writerId);
        Comment comment = commentFindService.findById(commentId);
        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadBoardFiles(image);

        Reply reply = Reply.createReply(writer, comment, imageUrl, content);

        return replyRepository.save(reply).getId();
    }

    @Transactional
    public void update(Long writerId, Long replyId, MultipartFile image, String content) {
        validateWriter(replyId, writerId);
        Reply reply = replyFindService.findById(replyId);
        String imageUrl = null;
        if (image != null)
            imageUrl = fileService.uploadBoardFiles(image);

        reply.updateImage(imageUrl);
        reply.updateContent(content);
    }

    @Transactional
    public void delete(Long writerId, Long replyId) {
        validateWriter(replyId, writerId);
        replyRepository.deleteById(replyId);
    }

    @Transactional
    public void deleteByWriter(User writer) {
        replyRepository.deleteByWriter(writer);
    }

    private void validateWriter(Long replyId, Long writerId) {
        Reply reply = replyFindService.findById(replyId);
        if (!reply.getWriter().getId().equals(writerId)) {
            throw BaseException.type(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER);
        }
    }
}
