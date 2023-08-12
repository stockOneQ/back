package umc.stockoneqback.board.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BoardList {
    private final Long id;
    private final String title;
    private final String content;
    private final int hit;
    private final LocalDateTime createdDate;
    private final int comment;
    private final int likes;

    @QueryProjection
    public BoardList(Long id, String title, String content, int hit, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hit = hit;
        this.createdDate = createdDate;
        this.comment = 0;
        this.likes = 0;
    }
}
