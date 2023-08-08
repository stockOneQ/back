package umc.stockoneqback.business.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FindBusinessUser {
    private final Long id;
    private final String name;

    @QueryProjection
    public FindBusinessUser(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
