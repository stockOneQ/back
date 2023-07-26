package umc.stockoneqback.product.dto.response;

import java.util.List;

public record GetListOfPassProductByOnlineUsersResponse(
        Long userId,

        List<String> productNameList
) {
}
