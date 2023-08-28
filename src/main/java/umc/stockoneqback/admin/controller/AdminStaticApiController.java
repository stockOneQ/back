package umc.stockoneqback.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.admin.dto.request.AddFARequest;
import umc.stockoneqback.admin.service.AdminStaticService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminStaticApiController {
    private final AdminStaticService AdminStaticService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/fa")
    public ResponseEntity<Void> addFA(@ExtractPayload Long userId,
                                      @Valid @RequestBody AddFARequest addFARequest) {
        AdminStaticService.addFA(addFARequest);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/fa")
    public ResponseEntity<Void> deleteFA(@ExtractPayload Long userId,
                                         @RequestParam(value = "question") String question) {
        AdminStaticService.deleteFA(question);
        return ResponseEntity.ok().build();
    }
}
