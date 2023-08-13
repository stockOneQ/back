package umc.stockoneqback.business.infra.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FilteredBusinessUser<T> {
    long total;
    List<T> businessUserList;

    public FilteredBusinessUser(long total, List<T> businessUserList) {
        this.total = total;
        this.businessUserList = businessUserList;
    }
}
