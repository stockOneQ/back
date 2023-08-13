package umc.stockoneqback.share.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.business.infra.query.dto.FilteredBusinessUser;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.service.ShareListService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/share")
public class ShareListApiController {
    private final ShareListService shareListService;

    @GetMapping("/users")
    public ResponseEntity<FilteredBusinessUser> userSelectBox(@ExtractPayload Long userId) {
        return ResponseEntity.ok(shareListService.userSelectBox(userId));
    }

    @GetMapping("")
    public ResponseEntity<CustomShareListPage> shareList(@ExtractPayload Long userId,
                                                         @RequestParam(value = "user") Long userBusinessId,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "category", required = false, defaultValue = "공지사항") String category,
                                                         @RequestParam(value = "search", required = false, defaultValue = "제목") String searchType,
                                                         @RequestParam(value = "word", required = false, defaultValue = "") String searchWord) throws IOException {
        return ResponseEntity.ok(shareListService.getShareList(userId, userBusinessId, page, category, searchType, searchWord));
    }
}
