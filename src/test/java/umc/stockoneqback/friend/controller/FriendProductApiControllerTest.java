package umc.stockoneqback.friend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.friend.exception.FriendErrorCode;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.exception.GlobalErrorCode;
import umc.stockoneqback.product.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductOthersResponse;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
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
import static umc.stockoneqback.fixture.ProductFixture.*;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("Friend [Controller Layer] -> FriendProductApiController 테스트")
public class FriendProductApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("공통 예외")
    class commonError {
        private static final String BASE_URL = "/api/friend/product/count";
        private static final String STORE_CONDITION = "상온";
        private static final Long FRIEND_ID = 2L;
        private static final String ERROR_STORE_CONDITION = "고온";

        @Test
        @DisplayName("권한이 없는 사용자가 FriendProduct API를 호출한 경우 API 호출에 실패한다")
        void throwExceptionByUnauthorizedUser() throws Exception {
            // given
            doThrow(BaseException.type(GlobalErrorCode.INVALID_USER_JWT))
                    .when(friendProductService)
                    .getTotalProductOthers(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.INVALID_USER_JWT;
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
                                    "FriendProductApi/CommonError/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
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
        @DisplayName("입력된 사용자가 요청한 사용자와 친구가 아닌 경우 API 호출에 실패한다")
        void throwExceptionByConflictFriend() throws Exception {
            // given
            doThrow(BaseException.type(FriendErrorCode.FRIEND_NOT_FOUND))
                    .when(friendProductService)
                    .getTotalProductOthers(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final FriendErrorCode expectedError = FriendErrorCode.FRIEND_NOT_FOUND;
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
                                    "FriendProductApi/CommonError/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
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
        @DisplayName("입력된 사용자가 유효하지 않은 역할을 가지고 있는 경우 API 호출에 실패한다")
        void throwExceptionByInvalidUser() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.ROLE_NOT_FOUND))
                    .when(friendProductService)
                    .getTotalProductOthers(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.ROLE_NOT_FOUND;
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
                                    "FriendProductApi/CommonError/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
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
        @DisplayName("입력된 친구의 가게를 불러올 수 없는 경우 API 호출에 실패한다")
        void throwExceptionByConflictFriendAndStore() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_STORE_MATCH_FAIL))
                    .when(friendProductService)
                    .getTotalProductOthers(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final UserErrorCode expectedError = UserErrorCode.USER_STORE_MATCH_FAIL;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "FriendProductApi/CommonError/Case4",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
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
        @DisplayName("입력된 보관방법이 잘못된 경우 API 호출에 실패한다")
        void throwExceptionByInvalidStoreCondition() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_STORE_CONDITION))
                    .when(friendProductService)
                    .getTotalProductOthers(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", ERROR_STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final ProductErrorCode expectedError = ProductErrorCode.NOT_FOUND_STORE_CONDITION;
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
                                    "FriendProductApi/CommonError/Case5",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("친구 가게의 입력된 이름을 포함하는 모든 제품 목록 조회 API [GET /api/friend/product/search]")
    class getProductOthersByNameIncludeInput {
        private static final String BASE_URL = "/api/friend/product/search";
        private static final String STORE_CONDITION = "상온";
        private static final Long FRIEND_ID = 2L;
        private static final String NAME = "리";
        private static final String ERROR_NAME = "텐트";

        @Test
        @DisplayName("입력된 이름을 포함하는 제품이 존재하지 않을 경우 친구 가게의 입력된 이름을 포함하는 모든 제품 목록 조회에 실패한다")
        void throwExceptionByInvalidProductName() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT))
                    .when(friendProductService)
                    .searchProductOthers(anyLong(), anyLong(), anyString(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .param("name", ERROR_NAME)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final ProductErrorCode expectedError = ProductErrorCode.NOT_FOUND_PRODUCT;
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
                                    "FriendProductApi/GetProductOthersByName/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("name").description("검색할 제품명")
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
        @DisplayName("친구 가게의 입력된 이름을 포함하는 모든 제품 목록 조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(searchProductOthersResponse())
                    .when(friendProductService)
                    .searchProductOthers(anyLong(), anyLong(), anyString(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .param("name", NAME)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result[0]").exists(),
                            jsonPath("$.result[0].name").value(DURIAN.getName()),
                            jsonPath("$.result[0].stockQuant").value(DURIAN.getStockQuant()),
                            jsonPath("$.result[1]").exists(),
                            jsonPath("$.result[1].name").value(BLUEBERRY.getName()),
                            jsonPath("$.result[1].stockQuant").value(BLUEBERRY.getStockQuant()),
                            jsonPath("$.result[2]").exists(),
                            jsonPath("$.result[2].name").value(CHERRY.getName()),
                            jsonPath("$.result[2].stockQuant").value(CHERRY.getStockQuant()),
                            jsonPath("$.result[3]").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "FriendProductApi/GetProductOthersByName/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("name").description("검색할 제품명")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result[].id").type(JsonFieldType.NUMBER).description("제품 ID"),
                                            fieldWithPath("result[].name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("result[].stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("result[].image").type(JsonFieldType.ARRAY).description("제품 이미지").optional()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("친구 가게의 분류 기준별 제품 개수 조회 API [GET /api/friend/product/count]")
    class getTotalProduct {
        private static final String BASE_URL = "/api/friend/product/count";
        private static final String STORE_CONDITION = "상온";
        private static final Long FRIEND_ID = 2L;

        @Test
        @DisplayName("친구 가게의 분류 기준별 제품 개수 조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(getTotalProductResponse())
                    .when(friendProductService)
                    .getTotalProductOthers(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result[0]").exists(),
                            jsonPath("$.result[0].name").value("Total"),
                            jsonPath("$.result[0].total").value(16),
                            jsonPath("$.result[1]").exists(),
                            jsonPath("$.result[1].name").value("Pass"),
                            jsonPath("$.result[1].total").value(2),
                            jsonPath("$.result[2]").exists(),
                            jsonPath("$.result[2].name").value("Close"),
                            jsonPath("$.result[2].total").value(5),
                            jsonPath("$.result[3]").exists(),
                            jsonPath("$.result[3].name").value("Lack"),
                            jsonPath("$.result[3].total").value(5),
                            jsonPath("$.result[4]").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "FriendProductApi/GetTotal/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result[].name").type(JsonFieldType.STRING).description("분류 기준"),
                                            fieldWithPath("result[].total").type(JsonFieldType.NUMBER).description("제품의 총 개수")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("친구 가게의 특정 정렬 기준 및 탐색 조건을 만족하는 제품 리스트 조회 API [GET /api/friend/product/page]")
    class getSearchProductOthers {
        private static final String BASE_URL = "/api/friend/product/page";
        private static final String STORE_CONDITION = "상온";
        private static final Long FRIEND_ID = 2L;
        private static final String SEARCH_CONDITION = "전체";
        private static final Long PRODUCT_ID = -1L;
        private static final List<ProductFixture> productFixtureList = new ArrayList<>(
                Stream.of(PERSIMMON, TANGERINE, DURIAN, MANGO, MELON,
                        BANANA, PEAR, PEACH, BLUEBERRY).collect(Collectors.toList())
        );

        @Test
        @DisplayName("친구 가게의 특정 정렬 기준 및 탐색 조건을 만족하는 제품 리스트 조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(searchProductOthersPageResponse())
                    .when(friendProductService)
                    .getListOfSearchProductOthers(anyLong(), eq(FRIEND_ID), eq(STORE_CONDITION), anyLong(), eq(SEARCH_CONDITION));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("friend", String.valueOf(FRIEND_ID))
                    .param("condition", STORE_CONDITION)
                    .param("search", SEARCH_CONDITION)
                    .param("last", String.valueOf(PRODUCT_ID))
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result[0]").exists(),
                            jsonPath("$.result[0].name").value(productFixtureList.get(0).getName()),
                            jsonPath("$.result[0].stockQuant").value(productFixtureList.get(0).getStockQuant()),
                            jsonPath("$.result[1]").exists(),
                            jsonPath("$.result[1].name").value(productFixtureList.get(1).getName()),
                            jsonPath("$.result[1].stockQuant").value(productFixtureList.get(1).getStockQuant()),
                            jsonPath("$.result[2]").exists(),
                            jsonPath("$.result[2].name").value(productFixtureList.get(2).getName()),
                            jsonPath("$.result[2].stockQuant").value(productFixtureList.get(2).getStockQuant()),
                            jsonPath("$.result[3]").exists(),
                            jsonPath("$.result[3].name").value(productFixtureList.get(3).getName()),
                            jsonPath("$.result[3].stockQuant").value(productFixtureList.get(3).getStockQuant()),
                            jsonPath("$.result[4]").exists(),
                            jsonPath("$.result[4].name").value(productFixtureList.get(4).getName()),
                            jsonPath("$.result[4].stockQuant").value(productFixtureList.get(4).getStockQuant()),
                            jsonPath("$.result[5]").exists(),
                            jsonPath("$.result[5].name").value(productFixtureList.get(5).getName()),
                            jsonPath("$.result[5].stockQuant").value(productFixtureList.get(5).getStockQuant()),
                            jsonPath("$.result[6]").exists(),
                            jsonPath("$.result[6].name").value(productFixtureList.get(6).getName()),
                            jsonPath("$.result[6].stockQuant").value(productFixtureList.get(6).getStockQuant()),
                            jsonPath("$.result[7]").exists(),
                            jsonPath("$.result[7].name").value(productFixtureList.get(7).getName()),
                            jsonPath("$.result[7].stockQuant").value(productFixtureList.get(7).getStockQuant()),
                            jsonPath("$.result[8]").exists(),
                            jsonPath("$.result[8].name").value(productFixtureList.get(8).getName()),
                            jsonPath("$.result[8].stockQuant").value(productFixtureList.get(8).getStockQuant()),
                            jsonPath("$.result[9]").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "FriendProductApi/GetPage/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("friend").description("요청한 친구 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("search").description("현재 설정된 탐색 조건"),
                                            parameterWithName("last").description("마지막 제품 ID(첫 요청 때에는 -1)")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result[].id").type(JsonFieldType.NUMBER).description("제품 ID"),
                                            fieldWithPath("result[].name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("result[].stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("result[].image").type(JsonFieldType.ARRAY).description("제품 이미지").optional()
                                    )
                            )
                    );
        }
    }

    private List<SearchProductOthersResponse> searchProductOthersResponse() {
        List<SearchProductOthersResponse> searchProductOthersResponseList = new ArrayList<>();
        searchProductOthersResponseList.add(new SearchProductOthersResponse(4L, DURIAN.getName(), DURIAN.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(11L, BLUEBERRY.getName(), BLUEBERRY.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(8L, CHERRY.getName(), CHERRY.getStockQuant(), null));
        return searchProductOthersResponseList;
    }

    private List<GetTotalProductResponse> getTotalProductResponse() {
        List<GetTotalProductResponse> getTotalProductResponseList = new ArrayList<>();
        getTotalProductResponseList.add(new GetTotalProductResponse("Total", 16));
        getTotalProductResponseList.add(new GetTotalProductResponse("Pass", 2));
        getTotalProductResponseList.add(new GetTotalProductResponse("Close", 5));
        getTotalProductResponseList.add(new GetTotalProductResponse("Lack", 5));
        return getTotalProductResponseList;
    }

    private List<SearchProductOthersResponse> searchProductOthersPageResponse() {
        List<SearchProductOthersResponse> searchProductOthersResponseList = new ArrayList<>();
        searchProductOthersResponseList.add(new SearchProductOthersResponse(16L, PERSIMMON.getName(), PERSIMMON.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(15L, TANGERINE.getName(), TANGERINE.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(4L, DURIAN.getName(), DURIAN.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(3L, MANGO.getName(), MANGO.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(9L, MELON.getName(), MELON.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(2L, BANANA.getName(), BANANA.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(17L, PEAR.getName(), PEAR.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(13L, PEACH.getName(), PEACH.getStockQuant(), null));
        searchProductOthersResponseList.add(new SearchProductOthersResponse(11L, BLUEBERRY.getName(), BLUEBERRY.getStockQuant(), null));
        return searchProductOthersResponseList;
    }
}
