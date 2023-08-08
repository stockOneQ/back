package umc.stockoneqback.share.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.share.controller.dto.ShareListResponse;
import umc.stockoneqback.share.service.ShareListService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/share")
public class ShareListApiController {
    private final ShareListService shareListService;

    @GetMapping("/{selectedUserId}")
    public ResponseEntity<ShareListResponse> boardList(@ExtractPayload Long userId,
                                                       @PathVariable(required = false) Long selectedUserId,
                                                       @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                       @RequestParam(value = "category", required = false, defaultValue = "공지사항") String category,
                                                       @RequestParam(value = "search", required = false, defaultValue = "제목") String searchBy,
                                                       @RequestParam(value = "word", required = false, defaultValue = "") String searchWord) throws IOException {
        return ResponseEntity.ok(shareListService.getShareList(userId, selectedUserId, page, category, searchBy, searchWord));
    }
}
