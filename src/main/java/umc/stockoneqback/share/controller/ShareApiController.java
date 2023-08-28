package umc.stockoneqback.share.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.share.controller.dto.ShareRequest;
import umc.stockoneqback.share.controller.dto.ShareResponse;
import umc.stockoneqback.share.service.ShareService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/share")
public class ShareApiController {
    private final ShareService shareService;

    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping("/{businessId}")
    public ResponseEntity<Void> create(@ExtractPayload Long userId,
                                       @PathVariable("businessId") Long businessId,
                                       @RequestParam(value = "category") String category,
                                       @RequestPart(value = "request") @Valid ShareRequest request,
                                       @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        shareService.create(userId, businessId, category, request, multipartFile);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping("")
    public ResponseEntity<Void> update(@ExtractPayload Long userId,
                                       @RequestParam("id") Long shareId,
                                       @RequestPart(value = "request") @Valid ShareRequest request,
                                       @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        shareService.update(userId, shareId, request, multipartFile);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER', 'SUPERVISOR')")
    @GetMapping("/{shareId}")
    public ResponseEntity<ShareResponse> detail(@ExtractPayload Long userId,
                                                @PathVariable Long shareId) {
        return ResponseEntity.ok(shareService.detail(userId, shareId));
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @DeleteMapping("")
    public ResponseEntity<Void> delete(@ExtractPayload Long userId,
                                       @RequestParam List<Long> shareId) throws IOException {
        shareService.delete(userId, shareId);
        return ResponseEntity.ok().build();
    }
}
