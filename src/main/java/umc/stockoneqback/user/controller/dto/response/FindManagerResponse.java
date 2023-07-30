package umc.stockoneqback.user.controller.dto.response;

import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;

public record FindManagerResponse (
    List<FindManager> searchedUser
) {
}