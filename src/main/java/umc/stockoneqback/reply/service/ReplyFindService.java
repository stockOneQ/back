package umc.stockoneqback.reply.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.domain.Reply;
import umc.stockoneqback.reply.domain.ReplyRepository;
import umc.stockoneqback.reply.exception.ReplyErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReplyFindService {
    private final ReplyRepository replyRepository;

    public Reply findById(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> BaseException.type(ReplyErrorCode.REPLY_NOT_FOUND));
    }
}
