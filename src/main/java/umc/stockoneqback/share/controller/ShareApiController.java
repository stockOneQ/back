package umc.stockoneqback.share.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.share.controller.dto.ShareRequest;
import umc.stockoneqback.share.controller.dto.ShareResponse;
import umc.stockoneqback.share.service.ShareService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/share")
public class ShareApiController {
    private final ShareService shareService;

    @PostMapping("/{businessId}")
    public ResponseEntity<Void> create(@ExtractPayload Long userId,
                                       @PathVariable Long businessId,
                                       @RequestParam(value = "category") String category,
                                       @RequestPart(value = "request") @Valid ShareRequest request,
                                       @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        shareService.create(userId, businessId, category, request, multipartFile);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{shareId}")
    public ResponseEntity<Void> update(@ExtractPayload Long userId,
                                       @PathVariable Long shareId,
                                       @RequestPart(value = "request") @Valid ShareRequest request,
                                       @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        shareService.update(userId, shareId, request, multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shareId}")
    public ResponseEntity<ShareResponse> detail(@ExtractPayload Long userId,
                                                @PathVariable Long shareId) {
        return ResponseEntity.ok(shareService.detail(userId, shareId));
    }
}
