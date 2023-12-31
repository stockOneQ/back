package umc.stockoneqback.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.business.service.BusinessService;
import umc.stockoneqback.global.annotation.ExtractPayload;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business")
public class BusinessApiController {
    private final BusinessService businessService;

    @PreAuthorize("hasRole('SUPERVISOR')")
    @PostMapping("/{managerId}")
    public ResponseEntity<Void> register(@ExtractPayload Long supervisorId, @PathVariable Long managerId) {
        businessService.register(supervisorId, managerId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('SUPERVISOR')")
    @DeleteMapping("/{managerId}")
    public ResponseEntity<Void> cancel(@ExtractPayload Long supervisorId, @PathVariable Long managerId) {
        businessService.cancel(supervisorId, managerId);
        return ResponseEntity.ok().build();
    }
}
