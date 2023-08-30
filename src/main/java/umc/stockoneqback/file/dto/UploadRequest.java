package umc.stockoneqback.file.dto;

import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.file.utils.ValidateDir;

public record UploadRequest(
        @ValidateDir
        String dir,

        MultipartFile file
) {
}