package umc.stockoneqback.product.service.response;

import java.util.List;

public record GetListOfPassProductByOnlineUsersResponse(
        Long userId,
        List<String> productNameList
) {
}
