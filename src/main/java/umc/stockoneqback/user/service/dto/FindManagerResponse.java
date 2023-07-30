package umc.stockoneqback.user.service.dto;

import java.util.List;

public record FindManagerResponse (
    List<FindManager> searchedUser
) {
}