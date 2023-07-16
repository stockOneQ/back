package umc.stockoneqback.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.file.utils.exception.FileErrorCode;
import umc.stockoneqback.global.exception.ApplicationException;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private static final String BOARD = "board";
    private static final String SHARE = "share";

    private final AmazonS3 amazonS3; // aws s3 client

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 게시글 작성 시 첨부파일 업로드
    public String uploadBoardFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(BOARD, file);
    }

    // 자료 공유 첨부파일 업로드
    public String uploadShareFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(SHARE, file);
    }

    // 파일 존재 여부 검증
    private void validateFileExists(MultipartFile file) {
        if (file == null) {
            throw ApplicationException.type(FileErrorCode.EMPTY_FILE);
        }
    }

    // upload to S3
    private String uploadFile(String dir, MultipartFile file) {
        String filePath = createFilePath(dir, file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, filePath, file.getInputStream(), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            throw ApplicationException.type(FileErrorCode.S3_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, filePath).toString();
    }

    // uuid 사용하여 fileKey(파일명) 생성
    private String createFilePath(String dir, String originalFileName) {
        String fileKey = UUID.randomUUID() + "_" + originalFileName;

        return switch (dir) {
            case BOARD -> String.format("board/%s", fileKey);
            case SHARE -> String.format("share/%s", fileKey);
            default -> throw ApplicationException.type(FileErrorCode.INVALID_DIR);
        };
    }
}
