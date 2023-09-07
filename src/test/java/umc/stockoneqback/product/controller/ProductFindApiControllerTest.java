package umc.stockoneqback.product.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductResponse;
import umc.stockoneqback.role.exception.StoreErrorCode;

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

@DisplayName("Product [Controller Layer] -> ProductFindApiController 테스트")
public class ProductFindApiControllerTest extends ControllerTest {
    private static final Long ERROR_STORE_ID = Long.MAX_VALUE;
    private static final String ERROR_STORE_CONDITION = "고온";
    private static final String ERROR_SORT_CONDITION = "제품 위치";
    private static final String ERROR_SEARCH_CONDITION = "전챼";

    @Nested
    @DisplayName("입력된 이름을 포함하는 모든 제품 목록 조회 API [GET /api/product/search]")
    class getProductByNameIncludeInput {
        private static final String BASE_URL = "/api/product/search";
        private static final String STORE_CONDITION = "상온";
        private static final Long STORE_ID = 1L;
        private static final String NAME = "리";

        @Test
        @DisplayName("유효하지 않은 가게 ID를 입력받으면 입력된 이름을 포함하는 모든 제품 목록 조회에 실패한다")
        void throwExceptionByInvalidStore() throws Exception {
            // given
            doThrow(BaseException.type(StoreErrorCode.STORE_NOT_FOUND))
                    .when(productFindService)
                    .searchProduct(anyLong(), anyLong(), anyString(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(ERROR_STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .param("name", NAME)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final StoreErrorCode expectedError = StoreErrorCode.STORE_NOT_FOUND;
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
                                    "ProductApi/GetProductByName/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
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
        @DisplayName("유효하지 않은 보관 방법을 입력받으면 입력된 이름을 포함하는 모든 제품 목록 조회에 실패한다")
        void throwExceptionByInvalidCondition() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_STORE_CONDITION))
                    .when(productFindService)
                    .searchProduct(anyLong(), anyLong(), anyString(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", ERROR_STORE_CONDITION)
                    .param("name", NAME)
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
                                    "ProductApi/GetProductByName/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
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
        @DisplayName("입력된 이름을 포함하는 모든 제품 목록 조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(searchProductResponse())
                    .when(productFindService)
                    .searchProduct(anyLong(), anyLong(), anyString(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .param("name", NAME)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result[0]").exists(),
                            jsonPath("$.result[0].name").value(DURIAN.getName()),
                            jsonPath("$.result[1]").exists(),
                            jsonPath("$.result[1].name").value(BLUEBERRY.getName()),
                            jsonPath("$.result[2]").exists(),
                            jsonPath("$.result[2].name").value(CHERRY.getName()),
                            jsonPath("$.result[3]").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "ProductApi/GetProductByName/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("name").description("검색할 제품명")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result[].id").type(JsonFieldType.NUMBER).description("제품 ID"),
                                            fieldWithPath("result[].name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("result[].image").type(JsonFieldType.ARRAY).description("제품 이미지").optional()
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("분류 기준별 제품 개수 조회 API [GET /api/product/count]")
    class getTotalProduct {
        private static final String BASE_URL = "/api/product/count";
        private static final String STORE_CONDITION = "상온";
        private static final Long STORE_ID = 1L;

        @Test
        @DisplayName("유효하지 않은 가게 ID를 입력받으면 분류 기준별 제품 개수 조회에 실패한다")
        void throwExceptionByInvalidStore() throws Exception {
            // given
            doThrow(BaseException.type(StoreErrorCode.STORE_NOT_FOUND))
                    .when(productFindService)
                    .getTotalProduct(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(ERROR_STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final StoreErrorCode expectedError = StoreErrorCode.STORE_NOT_FOUND;
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
                                    "ProductApi/GetTotal/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
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
        @DisplayName("유효하지 않은 보관 방법을 입력받으면 분류 기준별 제품 개수 조회에 실패한다")
        void throwExceptionByInvalidCondition() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_STORE_CONDITION))
                    .when(productFindService)
                    .getTotalProduct(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
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
                                    "ProductApi/GetTotal/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
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
        @DisplayName("분류 기준별 제품 개수 조회에 성공한다")
        void success() throws Exception{
            // given
            doReturn(getTotalProductResponse())
                    .when(productFindService)
                    .getTotalProduct(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
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
                                    "ProductApi/GetTotal/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
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
    @DisplayName("특정 정렬 기준 및 탐색 조건을 만족하는 제품 리스트 조회 API [GET /api/product/page]")
    class getAllProductWithSort {
        private static final String BASE_URL = "/api/product/page";
        private static final String STORE_CONDITION = "상온";
        private static final Long STORE_ID = 1L;
        private static final String SORT_CONDITION = "가나다";
        private static final String SEARCH_CONDITION = "전체";
        private static final Long PRODUCT_ID = -1L;
        private static final List<ProductFixture> productFixtureList = new ArrayList<>(
                Stream.of(PERSIMMON, TANGERINE, DURIAN, MANGO, MELON,
                        BANANA, PEAR, PEACH, BLUEBERRY, APPLE, WATERMELON, ORANGE).collect(Collectors.toList())
        );

        @Test
        @DisplayName("유효하지 않은 가게 ID를 입력받으면 특정 정렬 기준 및 탐색 조건을 만족하는 제품 리스트 조회에 실패한다")
        void throwExceptionByInvalidStore() throws Exception {
            // given
            doThrow(BaseException.type(StoreErrorCode.STORE_NOT_FOUND))
                    .when(productFindService)
                    .getListOfSearchProduct(anyLong(), eq(ERROR_STORE_ID), eq(STORE_CONDITION), eq(SEARCH_CONDITION), anyLong(), eq(SORT_CONDITION));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(ERROR_STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .param("search", SEARCH_CONDITION)
                    .param("last", String.valueOf(PRODUCT_ID))
                    .param("sort", SORT_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final StoreErrorCode expectedError = StoreErrorCode.STORE_NOT_FOUND;
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
                                    "ProductApi/GetPage/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("search").description("현재 설정된 탐색 조건"),
                                            parameterWithName("last").description("마지막 제품 ID(첫 요청 때에는 -1)"),
                                            parameterWithName("sort").description("정렬 기준")
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
        @DisplayName("유효하지 않은 보관 방법을 입력받으면 특정 정렬 기준을 만족하는 전체 제품 조회에 실패한다")
        void throwExceptionByInvalidCondition() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_STORE_CONDITION))
                    .when(productFindService)
                    .getListOfSearchProduct(anyLong(), eq(STORE_ID), eq(ERROR_STORE_CONDITION), eq(SEARCH_CONDITION), anyLong(), eq(SORT_CONDITION));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", ERROR_STORE_CONDITION)
                    .param("search", SEARCH_CONDITION)
                    .param("last", String.valueOf(PRODUCT_ID))
                    .param("sort", SORT_CONDITION)
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
                                    "ProductApi/GetPage/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("search").description("현재 설정된 탐색 조건"),
                                            parameterWithName("last").description("마지막 제품 ID(첫 요청 때에는 -1)"),
                                            parameterWithName("sort").description("정렬 기준")
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
        @DisplayName("유효하지 않은 탐색 조건을 입력받으면 특정 정렬 기준 및 탐색 조건을 만족하는 제품 리스트 조회에 실패한다")
        void throwExceptionByInvalidSearch() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_SEARCH_CONDITION))
                    .when(productFindService)
                    .getListOfSearchProduct(anyLong(), eq(STORE_ID), eq(STORE_CONDITION), eq(ERROR_SEARCH_CONDITION), anyLong(), eq(SORT_CONDITION));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .param("search", ERROR_SEARCH_CONDITION)
                    .param("last", String.valueOf(PRODUCT_ID))
                    .param("sort", SORT_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final ProductErrorCode expectedError = ProductErrorCode.NOT_FOUND_SEARCH_CONDITION;
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
                                    "ProductApi/GetPage/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("search").description("현재 설정된 탐색 조건"),
                                            parameterWithName("last").description("마지막 제품 ID(첫 요청 때에는 -1)"),
                                            parameterWithName("sort").description("정렬 기준")
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
        @DisplayName("유효하지 않은 정렬 방식을 입력받으면 특정 정렬 기준 및 탐색 조건을 만족하는 제품 리스트 조회에 실패한다")
        void throwExceptionByInvalidSort() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_SORT_CONDITION))
                    .when(productFindService)
                    .getListOfSearchProduct(anyLong(), eq(STORE_ID), eq(STORE_CONDITION), eq(SEARCH_CONDITION), anyLong(), eq(ERROR_SORT_CONDITION));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .param("search", SEARCH_CONDITION)
                    .param("last", String.valueOf(PRODUCT_ID))
                    .param("sort", ERROR_SORT_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final ProductErrorCode expectedError = ProductErrorCode.NOT_FOUND_SORT_CONDITION;
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
                                    "ProductApi/GetPage/Failure/Case4",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("search").description("현재 설정된 탐색 조건"),
                                            parameterWithName("last").description("마지막 제품 ID(첫 요청 때에는 -1)"),
                                            parameterWithName("sort").description("정렬 기준")
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
        @DisplayName("특정 정렬 기준 및 탐색 조건을 만족하는 제품 리스트 조회에 성공한다")
        void success() throws Exception{
            // given
            doReturn(searchProductPageResponse())
                    .when(productFindService)
                    .getListOfSearchProduct(anyLong(), eq(STORE_ID), eq(STORE_CONDITION), eq(SEARCH_CONDITION), anyLong(), eq(SORT_CONDITION));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .param("search", SEARCH_CONDITION)
                    .param("last", String.valueOf(PRODUCT_ID))
                    .param("sort", SORT_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result[0]").exists(),
                            jsonPath("$.result[0].name").value(productFixtureList.get(0).getName()),
                            jsonPath("$.result[1]").exists(),
                            jsonPath("$.result[1].name").value(productFixtureList.get(1).getName()),
                            jsonPath("$.result[2]").exists(),
                            jsonPath("$.result[2].name").value(productFixtureList.get(2).getName()),
                            jsonPath("$.result[3]").exists(),
                            jsonPath("$.result[3].name").value(productFixtureList.get(3).getName()),
                            jsonPath("$.result[4]").exists(),
                            jsonPath("$.result[4].name").value(productFixtureList.get(4).getName()),
                            jsonPath("$.result[5]").exists(),
                            jsonPath("$.result[5].name").value(productFixtureList.get(5).getName()),
                            jsonPath("$.result[6]").exists(),
                            jsonPath("$.result[6].name").value(productFixtureList.get(6).getName()),
                            jsonPath("$.result[7]").exists(),
                            jsonPath("$.result[7].name").value(productFixtureList.get(7).getName()),
                            jsonPath("$.result[8]").exists(),
                            jsonPath("$.result[8].name").value(productFixtureList.get(8).getName()),
                            jsonPath("$.result[9]").exists(),
                            jsonPath("$.result[9].name").value(productFixtureList.get(9).getName()),
                            jsonPath("$.result[10]").exists(),
                            jsonPath("$.result[10].name").value(productFixtureList.get(10).getName()),
                            jsonPath("$.result[11]").exists(),
                            jsonPath("$.result[11].name").value(productFixtureList.get(11).getName()),
                            jsonPath("$.result[12]").doesNotExist()
                    )
                    .andDo(
                            document(
                                    "ProductApi/GetPage/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법"),
                                            parameterWithName("search").description("현재 설정된 탐색 조건"),
                                            parameterWithName("last").description("마지막 제품 ID(첫 요청 때에는 -1)"),
                                            parameterWithName("sort").description("정렬 기준")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result[].id").type(JsonFieldType.NUMBER).description("제품 ID"),
                                            fieldWithPath("result[].name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("result[].image").type(JsonFieldType.ARRAY).description("제품 이미지").optional()
                                    )
                            )
                    );
        }
    }

    private List<SearchProductResponse> searchProductResponse() {
        return Stream.of(
                        new SearchProductResponse(4L, DURIAN.getName(), null),
                        new SearchProductResponse(11L, BLUEBERRY.getName(), null),
                        new SearchProductResponse(8L, CHERRY.getName(), null))
                .collect(Collectors.toList());
    }

    private List<GetTotalProductResponse> getTotalProductResponse() {
        return Stream.of(
                        new GetTotalProductResponse("Total", 16),
                        new GetTotalProductResponse("Pass", 2),
                        new GetTotalProductResponse("Close", 5),
                        new GetTotalProductResponse("Lack", 5))
                .collect(Collectors.toList());
    }

    private List<SearchProductResponse> searchProductPageResponse() {
        return Stream.of(
                        new SearchProductResponse(16L, PERSIMMON.getName(), null),
                        new SearchProductResponse(15L, TANGERINE.getName(), null),
                        new SearchProductResponse(4L, DURIAN.getName(), null),
                        new SearchProductResponse(3L, MANGO.getName(), null),
                        new SearchProductResponse(9L, MELON.getName(), null),
                        new SearchProductResponse(2L, BANANA.getName(), null),
                        new SearchProductResponse(17L, PEAR.getName(), null),
                        new SearchProductResponse(13L, PEACH.getName(), null),
                        new SearchProductResponse(11L, BLUEBERRY.getName(), null),
                        new SearchProductResponse(1L, APPLE.getName(), null),
                        new SearchProductResponse(10L, WATERMELON.getName(), null),
                        new SearchProductResponse(5L, ORANGE.getName(), null))
                .collect(Collectors.toList());
    }
}
