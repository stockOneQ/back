package umc.stockoneqback.reply.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.comment.domain.Comment;
import umc.stockoneqback.comment.service.CommentFindService;
import umc.stockoneqback.global.base.BaseException;
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

    @Transactional
    public Long create(Long writerId, Long commentId, String image, String content) {
        User writer = userFindService.findById(writerId);
        Comment comment = commentFindService.findById(commentId);
        Reply reply = Reply.createReply(writer, comment, image, content);

        return replyRepository.save(reply).getId();
    }

    @Transactional
    public void update(Long writerId, Long replyId, String image, String content) {
        validateWriter(replyId, writerId);
        Reply reply = replyFindService.findById(replyId);

        reply.updateImage(image);
        reply.updateContent(content);
    }

    @Transactional
    public void delete(Long writerId, Long replyId) {
        validateWriter(replyId, writerId);
        replyRepository.deleteById(replyId);
    }

    private void validateWriter(Long replyId, Long writerId) {
        Reply reply = replyFindService.findById(replyId);
        if (!reply.getWriter().getId().equals(writerId)) {
            throw BaseException.type(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER);
        }
    }
}
