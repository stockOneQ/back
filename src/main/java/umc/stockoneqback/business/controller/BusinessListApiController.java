package umc.stockoneqback.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.business.service.BusinessListService;
import umc.stockoneqback.business.service.dto.BusinessListResponse;
import umc.stockoneqback.global.annotation.ExtractPayload;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business")
public class BusinessListApiController {
    private final BusinessListService businessListService;

    @PreAuthorize("hasAnyRole('MANAGER', 'PART_TIMER')")
    @GetMapping("/supervisors")
    public ResponseEntity<BusinessListResponse> getSupervisors(@ExtractPayload Long userId,
                                                               @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId,
                                                               @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        BusinessListResponse response = businessListService.getSupervisors(userId, lastUserId, search);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @GetMapping("/managers")
    public ResponseEntity<BusinessListResponse> getManagers(@ExtractPayload Long userId,
                                                            @RequestParam(value = "last", required = false, defaultValue = "-1") Long lastUserId) {
        BusinessListResponse response = businessListService.getManagers(userId, lastUserId);
        return ResponseEntity.ok(response);
    }
}
