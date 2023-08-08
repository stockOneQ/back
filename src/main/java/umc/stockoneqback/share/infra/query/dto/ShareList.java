package umc.stockoneqback.share.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class ShareList {
    private final Long id;
    private final String title;
    private final LocalDate createdDate;
    private final String writer;
    private final String file;

    @QueryProjection
    public ShareList(Long id, String title, LocalDateTime createdDate, String writer, String file) {
        this.id = id;
        this.title = title;
        this.createdDate = createdDate.toLocalDate();
        this.writer = writer;
        this.file = file;
    }
}