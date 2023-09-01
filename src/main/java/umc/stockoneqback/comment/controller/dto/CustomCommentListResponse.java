package umc.stockoneqback.comment.controller.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record CustomCommentListResponse(
        CustomPageable pageInfo,
        List<CommentListResponse> CommentListResponse

) {
    @Getter
    @NoArgsConstructor
    public static class CustomPageable{
        long totalPages;
        long totalElements;
        boolean hasNext;
        long numberOfElements;

        public CustomPageable(long totalPages, long totalElements, boolean hasNext, long numberOfElements) {
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.hasNext = hasNext;
            this.numberOfElements = numberOfElements;
        }
    }
}
