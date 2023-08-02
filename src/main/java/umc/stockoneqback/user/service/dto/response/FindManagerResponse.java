package umc.stockoneqback.user.service.dto.response;

import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;

public record FindManagerResponse (
    List<FindManager> searchedUser
) {
}