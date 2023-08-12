package umc.stockoneqback.business.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FindBusinessUser {
    private final Long userBusinessId;
    private final Long userId;
    private final String name;

    @QueryProjection
    public FindBusinessUser(Long userBusinessId, Long userId, String name) {
        this.userBusinessId = userBusinessId;
        this.userId = userId;
        this.name = name;
    }
}
