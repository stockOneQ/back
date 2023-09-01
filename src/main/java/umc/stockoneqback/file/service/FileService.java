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
import umc.stockoneqback.file.exception.FileErrorCode;
import umc.stockoneqback.global.exception.BaseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private static final String BOARD = "board";
    private static final String SHARE = "share";
    private static final String PRODUCT = "product";
    private static final String COMMENT = "comment";
    private static final String REPLY = "reply";

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadBoardFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(BOARD, file);
    }

    public String uploadShareFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(SHARE, file);
    }

    public String uploadProductFiles(MultipartFile file) {
        validateFileExists(file);
        validateContentType(file);
        return uploadFile(PRODUCT, file);
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (!(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw BaseException.type(FileErrorCode.NOT_AN_IMAGE);
        }
    }

    public String uploadCommentFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(COMMENT, file);
    }

    public String uploadReplyFiles(MultipartFile file) {
        validateFileExists(file);
        return uploadFile(REPLY, file);
    }

    private void validateFileExists(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BaseException.type(FileErrorCode.EMPTY_FILE);
        }
    }

    private String uploadFile(String dir, MultipartFile file) {
        String fileKey = createFilePath(dir, file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        try {
            amazonS3.putObject(
                    new PutObjectRequest(bucket, fileKey, file.getInputStream(), objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            throw BaseException.type(FileErrorCode.S3_UPLOAD_FAILED);
        }

        String fullFileUrl = amazonS3.getUrl(bucket, fileKey).toString();

        return fileKey;
    }

    private String createFilePath(String dir, String originalFileName) {
        String uuidName = UUID.randomUUID() + "_" + originalFileName;

        return switch (dir) {
            case BOARD -> String.format("board/%s", uuidName);
            case SHARE -> String.format("share/%s", uuidName);
            case PRODUCT -> String.format("product/%s", uuidName);
            case COMMENT -> String.format("comment/%s", uuidName);
            case REPLY -> String.format("reply/%s", uuidName);
            default -> throw BaseException.type(FileErrorCode.INVALID_DIR);
        };
    }

    public ResponseEntity<byte[]> download(String fileKey) throws IOException {
        S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucket, fileKey));
        S3ObjectInputStream objectInputStream = s3object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType(fileKey));
        httpHeaders.setContentLength(bytes.length);
        String[] arr = fileKey.split("/");
        String type = arr[arr.length - 1];
        String fileName = URLEncoder.encode(type, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        httpHeaders.setContentDispositionFormData("attachment", fileName); // 파일 이름 지정

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    public byte[] downloadToResponseDto(String fileKey) throws IOException {
        S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucket, fileKey));
        S3ObjectInputStream objectInputStream = s3object.getObjectContent();
        return IOUtils.toByteArray(objectInputStream);
    }

    private MediaType contentType(String fileKey) {
        String[] arr = fileKey.split("\\.");
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
