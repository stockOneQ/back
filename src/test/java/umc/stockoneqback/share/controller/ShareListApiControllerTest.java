package umc.stockoneqback.share.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.share.exception.ShareErrorCode;
import umc.stockoneqback.share.infra.query.dto.CustomShareListPage;
import umc.stockoneqback.share.infra.query.dto.ShareList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static umc.stockoneqback.fixture.ShareFixture.*;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;
import static umc.stockoneqback.fixture.UserFixture.ANNE;

@DisplayName("Share [Controller Layer] -> ShareListApiController 테스트")
class ShareListApiControllerTest extends ControllerTest {
    private static final String INVALID_CATEGORY = "일정";
    private static final String INVALID_SEARCH = "글쓴이";
    private static final String CATEGORY = "공지사항";
    private static final String SEARCH_TYPE = "제목";
    private static final String SEARCH_WORD = "제목";
    private static final int PAGE = 0;

    @Nested
    @DisplayName("커넥트 - 자료 목록 조회 API [GET /api/share]")
    class shareList {
        private static final String BASE_URL = "/api/share";
        private static final Long USER_ID = 1L;
        private static final Long SELECTED_USER_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 자료 목록 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("user", String.valueOf(USER_ID))
                    .param("page", String.valueOf(PAGE))
                    .param("category", CATEGORY)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD);

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
                                    "ShareApi/List/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("user").description("선택된 유저의 id"),
                                            parameterWithName("page").description("페이지 번호"),
                                            parameterWithName("category").description("카테고리(공지사항/레시피/행사내용/기타)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)").optional(),
                                            parameterWithName("word").description("검색어").optional()
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("유효하지 않은 카테고리라면 자료 목록 조회에 실패한다")
        void throwExceptionByNotFoundSortCondition() throws Exception {
            // given
            doThrow(BaseException.type(ShareErrorCode.NOT_FOUND_CATEGORY))
                    .when(shareListService)
                    .getShareList(anyLong(), anyLong(), eq(PAGE), eq(INVALID_CATEGORY), eq(SEARCH_TYPE), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("user", String.valueOf(USER_ID))
                    .param("page", String.valueOf(PAGE))
                    .param("category", INVALID_CATEGORY)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final ShareErrorCode expectedError = ShareErrorCode.NOT_FOUND_CATEGORY;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "ShareApi/List/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("user").description("선택된 유저의 id"),
                                            parameterWithName("page").description("페이지 번호"),
                                            parameterWithName("category").description("카테고리(공지사항/레시피/행사내용/기타)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)").optional(),
                                            parameterWithName("word").description("검색어").optional()
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("유효하지 않은 검색 조건이라면 자료 검색에 실패한다")
        void throwExceptionByNotFoundSearchType() throws Exception {
            // given
            doThrow(BaseException.type(ShareErrorCode.NOT_FOUND_SEARCH_TYPE))
                    .when(shareListService)
                    .getShareList(anyLong(), anyLong(), eq(PAGE), eq(CATEGORY), eq(INVALID_SEARCH), eq(SEARCH_WORD));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("user", String.valueOf(USER_ID))
                    .param("page", String.valueOf(PAGE))
                    .param("category", CATEGORY)
                    .param("search", INVALID_SEARCH)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final ShareErrorCode expectedError = ShareErrorCode.NOT_FOUND_SEARCH_TYPE;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "ShareApi/List/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("user").description("선택된 유저의 id"),
                                            parameterWithName("page").description("페이지 번호"),
                                            parameterWithName("category").description("카테고리(공지사항/레시피/행사내용/기타)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)").optional(),
                                            parameterWithName("word").description("검색어").optional()
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("자료 목록 조회에 성공한다")
        void success() throws Exception{
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(USER_ID);
            doReturn(getCustomShareListPage())
                    .when(shareListService)
                    .getShareList(USER_ID, SELECTED_USER_ID, PAGE, CATEGORY, SEARCH_TYPE, SEARCH_WORD);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("user", String.valueOf(SELECTED_USER_ID))
                    .param("page", String.valueOf(PAGE))
                    .param("category", CATEGORY)
                    .param("search", SEARCH_TYPE)
                    .param("word", SEARCH_WORD)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ShareApi/List/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("user").description("선택된 유저의 id"),
                                            parameterWithName("page").description("페이지 번호"),
                                            parameterWithName("category").description("카테고리(공지사항/레시피/행사내용/기타)"),
                                            parameterWithName("search").description("검색 조건(제목/내용)"),
                                            parameterWithName("word").description("검색어").optional()
                                    ),
                                    responseFields(
                                            fieldWithPath("pageInfo.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호(1 페이지 = 0)"),
                                            fieldWithPath("pageInfo.pageSize").type(JsonFieldType.NUMBER).description("고정 페이지 사이즈(6 고정)"),
                                            fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                            fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 글 수"),
                                            fieldWithPath("pageInfo.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부(마지막 페이지만 false)"),
                                            fieldWithPath("pageInfo.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 글 수"),
                                            fieldWithPath("shareList[].id").type(JsonFieldType.NUMBER).description("글 ID"),
                                            fieldWithPath("shareList[].title").type(JsonFieldType.STRING).description("글 제목"),
                                            fieldWithPath("shareList[].createdDate").type(JsonFieldType.STRING).description("글 최초 등록 시간"),
                                            fieldWithPath("shareList[].writer").type(JsonFieldType.STRING).description("글 작성자"),
                                            fieldWithPath("shareList[].file").type(JsonFieldType.STRING).description("파일").optional()
                                    )
                            )
                    );
        }
    }

    private List<ShareList> createShareList() {
        List<ShareList> shareLists = new ArrayList<>();
        shareLists.add(new ShareList(1L, SHARE_1.getTitle(), LocalDateTime.now(), ANNE.getName(), SHARE_1.getFile()));
        shareLists.add(new ShareList(2L, SHARE_2.getTitle(), LocalDateTime.now(), ANNE.getName(), SHARE_2.getFile()));
        shareLists.add(new ShareList(3L, SHARE_3.getTitle(), LocalDateTime.now(), ANNE.getName(), SHARE_3.getFile()));
        shareLists.add(new ShareList(4L, SHARE_4.getTitle(), LocalDateTime.now(), ANNE.getName(), SHARE_4.getFile()));
        return shareLists;
    }

    private CustomShareListPage.CustomPageable createCustomPageable() {
        return new CustomShareListPage.CustomPageable(0, 6, 1, 4, false, 4);
    }

    private CustomShareListPage<ShareList> getCustomShareListPage() {
        return new CustomShareListPage<>(createCustomPageable(), createShareList());
    }
}