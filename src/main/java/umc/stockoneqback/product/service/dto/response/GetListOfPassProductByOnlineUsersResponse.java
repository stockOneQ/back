package umc.stockoneqback.product.service.dto.response;

import java.util.List;

public record GetListOfPassProductByOnlineUsersResponse(
        Long userId,
        List<String> productNameList
) {
}
