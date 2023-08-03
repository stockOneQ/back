package umc.stockoneqback.board.controller.dto;

import java.util.List;

public record SelectedMyBoardRequest (
        List<Long> SelectedBoardId
){
}