package umc.stockoneqback.user.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class FindManager {
    private final Long id;
    private final String name;
    private final String storeName;
    private final String phoneNumber;

    @QueryProjection
    public FindManager(Long id, String name, String storeName, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.storeName = storeName;
        this.phoneNumber = phoneNumber;
    }
}
