package umc.stockoneqback.file.dto;

import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.file.utils.validation.ValidateDir;

public record UploadRequest(
        @ValidateDir
        String dir,

        MultipartFile file
) {
}