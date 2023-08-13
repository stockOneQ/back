package umc.stockoneqback.share.infra.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomShareListPage<T> {
    CustomPageable pageInfo; // pageable
    List<T> shareList; // content

    public CustomShareListPage(Page<T> page) {
        this.shareList = page.getContent();
        this.pageInfo = new CustomPageable(
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.getNumberOfElements()
        );
    }

    @Getter
    @NoArgsConstructor
    public static class CustomPageable {
        int pageNumber;
        int pageSize;
        long totalPages;
        long totalElements;
        boolean hasNext;
        long numberOfElements;

        public CustomPageable(int pageNumber, int pageSize, long totalPages, long totalElements, boolean hasNext, long numberOfElements) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.hasNext = hasNext;
            this.numberOfElements = numberOfElements;
        }
    }
}