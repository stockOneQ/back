package umc.stockoneqback.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.user.controller.dto.request.FindLoginIdRequest;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.service.UserInformationService;
import umc.stockoneqback.user.service.dto.response.LoginIdResponse;
import umc.stockoneqback.user.service.dto.response.UserInformationResponse;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserInformationApiController {
    private final UserInformationService userInformationService;

    @GetMapping("/find-id")
    public ResponseEntity<LoginIdResponse> findLoginId(@RequestBody @Valid FindLoginIdRequest request) {
        LoginIdResponse response = userInformationService.findLoginId(request.name(), request.birth(), Email.from(request.email()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/information")
    public ResponseEntity<UserInformationResponse> getInformation(@ExtractPayload Long userId) {
        UserInformationResponse response = userInformationService.getInformation(userId);
        return ResponseEntity.ok(response);
    }
}
