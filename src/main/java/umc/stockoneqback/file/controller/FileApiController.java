package umc.stockoneqback.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.file.dto.UploadRequest;
import umc.stockoneqback.file.service.FileService;
import umc.stockoneqback.file.utils.exception.FileErrorCode;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseException;

import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {
    private final FileService fileService;

    // upload
    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@ExtractPayload Long userId, @ModelAttribute @Valid UploadRequest request) {
        String dir = request.dir();
        switch (dir) {
            case "board" -> {
                String uploadFileLink = fileService.uploadBoardFiles(request.file());
                return ResponseEntity.ok(uploadFileLink);
            }
            case "share" -> {
                String uploadFileLink = fileService.uploadShareFiles(request.file());
                return ResponseEntity.ok(uploadFileLink);
            }
            case "product" -> {
                String uploadFileLink = fileService.uploadProductFiles(request.file());
                return ResponseEntity.ok(uploadFileLink);
            }
            case "comment" -> {
                String uploadFileLink = fileService.uploadCommentFiles(request.file());
                return ResponseEntity.ok(uploadFileLink);
            }
            default -> throw new BaseException(FileErrorCode.INVALID_DIR);
        }
    }

    // download
    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> download(@ExtractPayload Long userId, String fileKey) throws IOException {
        return fileService.download(fileKey);
    }
}
