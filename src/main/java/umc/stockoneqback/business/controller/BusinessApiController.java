package umc.stockoneqback.business.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.business.service.BusinessService;
import umc.stockoneqback.global.annotation.ExtractPayload;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business")
public class BusinessApiController {
    private final BusinessService businessService;

    @PostMapping("/{managerId}")
    public ResponseEntity<Void> register(@ExtractPayload Long supervisorId, @PathVariable Long managerId) {
        businessService.register(supervisorId, managerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{managerId}")
    public ResponseEntity<Void> cancel(@ExtractPayload Long supervisorId, @PathVariable Long managerId) {
        businessService.cancel(supervisorId, managerId);
        return ResponseEntity.ok().build();
    }
}
