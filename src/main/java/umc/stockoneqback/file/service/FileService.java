package umc.stockoneqback.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.file.utils.exception.FileErrorCode;
import umc.stockoneqback.global.base.BaseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private static final String BOARD = "board";
    private static final String SHARE = "share";
    private static final String PRODUCT = "product";
    private static final String COMMENT = "comment";

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

    // 제품 사진 업로드
    public String uploadProductFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(PRODUCT, file);
    }

    public String uploadCommentFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(COMMENT, file);
    }

    // 파일 존재 여부 검증
    private void validateFileExists(MultipartFile file) {
        if (file == null) {
            throw BaseException.type(FileErrorCode.EMPTY_FILE);
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
            throw BaseException.type(FileErrorCode.S3_UPLOAD_FAILED);
        }

        return amazonS3.getUrl(bucket, filePath).toString();
    }

    // uuid 사용하여 fileKey(파일명) 생성
    private String createFilePath(String dir, String originalFileName) {
        String fileKey = UUID.randomUUID() + "_" + originalFileName;

        return switch (dir) {
            case BOARD -> String.format("board/%s", fileKey);
            case SHARE -> String.format("share/%s", fileKey);
            case PRODUCT -> String.format("product/%s", fileKey);
            case COMMENT -> String.format("comment/%s", fileKey);
            default -> throw BaseException.type(FileErrorCode.INVALID_DIR);
        };
    }

    // download
    public ResponseEntity<byte[]> download(String fileUrl) throws IOException {
        S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucket, fileUrl));
        S3ObjectInputStream objectInputStream = s3object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType(fileUrl));
        httpHeaders.setContentLength(bytes.length);
        String[] arr = fileUrl.split("/");
        String type = arr[arr.length - 1];
        String fileName = URLEncoder.encode(type, "UTF-8").replaceAll("\\+", "%20");
        httpHeaders.setContentDispositionFormData("attachment", fileName); // 파일 이름 지정

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    public byte[] downloadToResponseDto(String fileUrl) throws IOException {
        S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucket, fileUrl));
        S3ObjectInputStream objectInputStream = s3object.getObjectContent();
        return IOUtils.toByteArray(objectInputStream);
    }

    private MediaType contentType(String fileUrl) {
        String[] arr = fileUrl.split("\\.");
        String type = arr[arr.length - 1];

        switch (type) {
            case "txt" -> {
                return MediaType.TEXT_PLAIN;
            }
            case "jpg" -> {
                return MediaType.IMAGE_JPEG;
            }
            case "png" -> {
                return MediaType.IMAGE_PNG;
            }
            default -> {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
        }
    }
}
