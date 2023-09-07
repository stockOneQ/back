package umc.stockoneqback.reply.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.reply.controller.dto.ReplyRequest;
import umc.stockoneqback.reply.exception.ReplyErrorCode;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.common.DocumentFormatGenerator.getInputDTOFormat;
import static umc.stockoneqback.common.DocumentFormatGenerator.getInputImageFormat;
import static umc.stockoneqback.fixture.ReplyFixture.REPLY_0;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("Reply [Controller Layer] -> ReplyApiController 테스트")
public class ReplyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("대댓글 등록 API [POST /api/replies/{commentId}]")
    class createBoard {
        private static final String BASE_URL = "/api/replies/{commentId}";
        private static final Long WRITER_ID = 1L;
        private static final Long COMMENT_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 대댓글 등록에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final ReplyRequest request = createReplyRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("request", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, COMMENT_ID)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Create/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("commentId").description("등록할 댓글 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일"),
                                            partWithName("request").attributes(getInputDTOFormat()).description("등록할 대댓글 DTO")
                                    ),
                                    requestPartFields(
                                            "request",
                                            fieldWithPath("image").description("등록할 이미지"),
                                            fieldWithPath("content").description("등록할 내용")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("대댓글 등록에 성공한다")
        void success() throws Exception {
            // given
            doReturn(1L)
                    .when(replyService)
                    .create(anyLong(), any(), any(), any());

            // when
            final ReplyRequest request = createReplyRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("request", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, COMMENT_ID)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Create/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("commentId").description("등록할 댓글 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일"),
                                            partWithName("request").attributes(getInputDTOFormat()).description("등록할 대댓글 DTO")
                                    ),
                                    requestPartFields(
                                            "request",
                                            fieldWithPath("image").description("등록할 이미지"),
                                            fieldWithPath("content").description("등록할 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("대댓글 수정 API [PATCH /api/replies/{replyId}]")
    class updateBoard {
        private static final String BASE_URL = "/api/replies/{replyId}";
        private static final Long WRITER_ID = 1L;
        private static final Long REPLY_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 대댓글 수정에 실패한다")
        void withoutAccessToken() throws Exception {
            // given
            final ReplyRequest request = createReplyRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("request", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, REPLY_ID)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                            request.setMethod("PATCH");
                            return request;
                        }
                    });

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Update/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("replyId").description("수정할 대댓글 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일"),
                                            partWithName("request").attributes(getInputDTOFormat()).description("수정할 대댓글 DTO")
                                    ),
                                    requestPartFields(
                                            "request",
                                            fieldWithPath("image").description("수정할 이미지"),
                                            fieldWithPath("content").description("수정할 내용")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("다른 사람의 대댓글은 수정할 수 없다")
        void throwExceptionByUserIsNotReplyWriter() throws Exception {
            // given
            doThrow(BaseException.type(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER))
                    .when(replyService)
                    .update(anyLong(), anyLong(), any(), any());

            // when
            final ReplyRequest request = createReplyRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("request", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, REPLY_ID)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                            request.setMethod("PATCH");
                            return request;
                        }
                    });

            // then
            final ReplyErrorCode expectedError = ReplyErrorCode.USER_IS_NOT_REPLY_WRITER;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Update/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("replyId").description("수정할 대댓글 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일"),
                                            partWithName("request").attributes(getInputDTOFormat()).description("수정할 대댓글 DTO")
                                    ),
                                    requestPartFields(
                                            "request",
                                            fieldWithPath("image").description("수정할 이미지"),
                                            fieldWithPath("content").description("수정할 내용")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("대댓글 수정에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(replyService)
                    .update(anyLong(), anyLong(), any(), any());

            // when
            final ReplyRequest request = createReplyRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("request", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, REPLY_ID)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                            request.setMethod("PATCH");
                            return request;
                        }
                    });

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Update/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("replyId").description("수정할 대댓글 ID(PK)")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일"),
                                            partWithName("request").attributes(getInputDTOFormat()).description("수정할 대댓글 DTO")
                                    ),
                                    requestPartFields(
                                            "request",
                                            fieldWithPath("image").description("수정할 이미지"),
                                            fieldWithPath("content").description("수정할 내용")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("대댓글 삭제 API [DELETE /api/replies/{replyId}]")
    class deleteBoard {
        private static final String BASE_URL = "/api/replies/{replyId}";
        private static final Long WRITER_ID = 1L;
        private static final Long REPLY_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 대댓글 삭제에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, REPLY_ID)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Delete/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("replyId").description("삭제할 대댓글 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("다른 사람의 대댓글은 삭제할 수 없다")
        void throwExceptionByUserIsNotReplyWriter() throws Exception {
            // given
            doThrow(BaseException.type(ReplyErrorCode.USER_IS_NOT_REPLY_WRITER))
                    .when(replyService)
                    .delete(anyLong(), anyLong());

            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, WRITER_ID, REPLY_ID)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                            request.setMethod("DELETE");
                            return request;
                        }
                    });

            // then
            final ReplyErrorCode expectedError = ReplyErrorCode.USER_IS_NOT_REPLY_WRITER;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Delete/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("replyId").description("삭제할 대댓글 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("대댓글 삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(replyService)
                    .delete(anyLong(), anyLong());

            // when
            final ReplyRequest request = createReplyRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, REPLY_ID)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                            request.setMethod("DELETE");
                            return request;
                        }
                    });

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ReplyApi/Delete/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("replyId").description("삭제할 대댓글 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private ReplyRequest createReplyRequest() {
        return new ReplyRequest(REPLY_0.getImage(), REPLY_0.getContent());
    }
}
