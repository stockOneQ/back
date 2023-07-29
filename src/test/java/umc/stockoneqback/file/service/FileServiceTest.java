package umc.stockoneqback.file.service;

import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.file.config.S3MockConfig;
import umc.stockoneqback.file.utils.exception.FileErrorCode;
import umc.stockoneqback.global.base.BaseException;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(S3MockConfig.class)
@DisplayName("File [Service Layer] -> FileService 테스트")
class FileServiceTest extends ServiceTest {
    @Autowired
    private FileService fileService;

    @Autowired
    private S3Mock s3Mock;

    private String FILE_PATH = "src/test/resources/files/";

    @AfterEach
    public void tearDown() {
        s3Mock.stop();
    }

    @Nested
    @DisplayName("게시판 관련 파일 업로드")
    class uploadBoardFiles {
        @Test
        @DisplayName("빈 파일이면 업로드에 실패한다")
        void throwExceptionByEmptyFile() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("board", "empty.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> fileService.uploadBoardFiles(nullFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
            assertThatThrownBy(() -> fileService.uploadBoardFiles(emptyFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
        }

        @Test
        @DisplayName("파일 업로드에 성공한다")
        void success() throws Exception {
            // given
            String fileName = "test.png";
            String contentType = "image/png";
            String dir = "board";
            MultipartFile file = createMockMultipartFile(dir, fileName, contentType);

            // when
            String fileKey = fileService.uploadBoardFiles(file);

            // then
            assertThat(fileKey).contains("test.png");
            assertThat(fileKey).contains(dir);
        }
    }

    @Nested
    @DisplayName("자료 공유 관련 파일 업로드")
    class uploadShareFiles {
        @Test
        @DisplayName("빈 파일이면 업로드에 실패한다")
        void throwExceptionByEmptyFile() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("board", "empty.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> fileService.uploadShareFiles(nullFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
            assertThatThrownBy(() -> fileService.uploadShareFiles(emptyFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
        }

        @Test
        @DisplayName("파일 업로드에 성공한다")
        void success() throws Exception {
            // given
            String fileName = "test.png";
            String contentType = "image/png";
            String dir = "share";
            MultipartFile file = createMockMultipartFile(dir, fileName, contentType);

            // when
            String fileKey = fileService.uploadShareFiles(file);

            // then
            assertThat(fileKey).contains("test.png");
            assertThat(fileKey).contains(dir);
        }
    }

    @Nested
    @DisplayName("제품 관련 파일 업로드")
    class uploadProductFiles {
        @Test
        @DisplayName("빈 파일이면 업로드에 실패한다")
        void throwExceptionByEmptyFile() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("board", "empty.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> fileService.uploadProductFiles(nullFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
            assertThatThrownBy(() -> fileService.uploadProductFiles(emptyFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
        }

        @Test
        @DisplayName("이미지 파일이 아니면 업로드에 실패한다")
        void throwExceptionByNotAnImage() throws IOException {
            // given
            String fileName = "test.txt";
            String contentType = "text/plain";
            String dir = "product";
            MultipartFile file = createMockMultipartFile(dir, fileName, contentType);

            // when - then
            assertThatThrownBy(() -> fileService.uploadProductFiles(file))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.NOT_AN_IMAGE.getMessage());
        }

        @Test
        @DisplayName("파일 업로드에 성공한다")
        void success() throws Exception {
            // given
            String fileName = "test.png";
            String contentType = "image/png";
            String dir = "product";
            MultipartFile file = createMockMultipartFile(dir, fileName, contentType);

            // when
            String fileKey = fileService.uploadProductFiles(file);

            // then
            assertThat(fileKey).contains("test.png");
            assertThat(fileKey).contains(dir);
        }
    }

    @Nested
    @DisplayName("댓글 관련 파일 업로드")
    class uploadCommentFiles {
        @Test
        @DisplayName("빈 파일이면 업로드에 실패한다")
        void throwExceptionByEmptyFile() {
            // given
            MultipartFile nullFile = null;
            MultipartFile emptyFile = new MockMultipartFile("board", "empty.png", "image/png", new byte[]{});

            // when - then
            assertThatThrownBy(() -> fileService.uploadCommentFiles(nullFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
            assertThatThrownBy(() -> fileService.uploadCommentFiles(emptyFile))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(FileErrorCode.EMPTY_FILE.getMessage());
        }

        @Test
        @DisplayName("파일 업로드에 성공한다")
        void success() throws Exception {
            // given
            String fileName = "test.png";
            String contentType = "image/png";
            String dir = "comment";
            MultipartFile file = createMockMultipartFile(dir, fileName, contentType);

            // when
            String fileKey = fileService.uploadCommentFiles(file);

            // then
            assertThat(fileKey).contains("test.png");
            assertThat(fileKey).contains(dir);
        }
    }

    @Nested
    @DisplayName("파일 다운로드")
    class download {
        @Test
        @DisplayName("파일 다운로드 및 응답에 성공한다")
        void success() throws Exception {
            // given
            String fileName = "test.png";
            String contentType = "image/png";
            String dir = "board";

            MultipartFile file = createMockMultipartFile(dir, fileName, contentType);
            String fileKey = fileService.uploadBoardFiles(file);

            // when
            byte[] responseByBytes = fileService.downloadToResponseDto(fileKey);
            ResponseEntity<byte[]> downloadResponse = fileService.download(fileKey);

            // then
            assertAll(
                    () -> assertThat(downloadResponse.getBody().equals(file)),
                    () -> assertThat(downloadResponse.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG),
                    () -> assertThat(downloadResponse.getStatusCodeValue()).isEqualTo(200)
            );
        }
    }

    @Nested
    @DisplayName("byte 배열로 파일 반환")
    class downloadToResponseDto {
        @Test
        @DisplayName("byte 배열 형태로 다운로드에 성공한다")
        void success() throws Exception {
            // given
            String fileName = "test.png";
            String contentType = "image/png";
            String dir = "product";

            MultipartFile file = createMockMultipartFile(dir, fileName, contentType);
            String fileKey = fileService.uploadProductFiles(file);

            // when
            byte[] responseByBytes = fileService.downloadToResponseDto(fileKey);

            // then
            assertThat(responseByBytes.equals(file));
        }
    }

    private MultipartFile createMockMultipartFile(String dir, String fileName, String contentType) throws IOException {
        try (FileInputStream stream = new FileInputStream(FILE_PATH + fileName)) {
            return new MockMultipartFile(dir, fileName, contentType, stream);
        }
    }
}