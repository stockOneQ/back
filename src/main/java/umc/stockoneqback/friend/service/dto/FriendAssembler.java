package umc.stockoneqback.friend.service.dto;

import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;

import java.util.List;

public record FriendAssembler(
        List<FriendInformation> friends
) {
}
