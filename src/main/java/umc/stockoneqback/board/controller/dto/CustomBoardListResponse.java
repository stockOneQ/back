package umc.stockoneqback.board.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomBoardListResponse<T> {
    CustomPageable pageInfo; // pageable
    List<T> boardList; // content

    public CustomBoardListResponse(Page<T> page) {
        this.boardList = page.getContent();
        this.pageInfo = new CustomPageable(
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.getNumberOfElements()
        );
    }

    @Getter
    @NoArgsConstructor
    public static class CustomPageable {
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