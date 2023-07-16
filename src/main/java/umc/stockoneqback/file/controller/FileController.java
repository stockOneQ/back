package umc.stockoneqback.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.stockoneqback.file.dto.UploadRequest;
import umc.stockoneqback.file.service.FileService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileController {
    private final FileService fileService;

    // upload
    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@ModelAttribute @Valid UploadRequest request) {
        String uploadFileLink = request.dir().equals("board")
                ? fileService.uploadBoardFiles(request.file())
                : fileService.uploadShareFiles(request.file());

        return ResponseEntity.ok(uploadFileLink);
    }
}
