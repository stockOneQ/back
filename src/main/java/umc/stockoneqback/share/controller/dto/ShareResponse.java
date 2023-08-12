package umc.stockoneqback.share.controller.dto;

import umc.stockoneqback.share.domain.Share;

public record ShareResponse(
        Long id,
        String title,
        String file,
        String content,
        Boolean isWriter // 작성자 판별
) {
    public static ShareResponse toResponse (Share share, Boolean isWriter) {
        return new ShareResponse(share.getId(), share.getTitle(), share.getFile(), share.getContent(), isWriter);
    }
}
