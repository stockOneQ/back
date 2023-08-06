package umc.stockoneqback.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.admin.dto.request.AddFARequest;
import umc.stockoneqback.admin.service.AdminStaticService;
import umc.stockoneqback.global.annotation.ExtractPayload;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminStaticApiController {
    private final AdminStaticService adminStaticService;

    @PostMapping("/fa")
    public ResponseEntity<Void> addFA(@ExtractPayload Long userId,
                                      @Valid @RequestBody AddFARequest addFARequest) {
        adminStaticService.addFA(userId, addFARequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/fa")
    public ResponseEntity<Void> deleteFA(@ExtractPayload Long userId,
                                      @RequestParam(value = "question") String question) {
        adminStaticService.deleteFA(userId, question);
        return ResponseEntity.ok().build();
    }
}
