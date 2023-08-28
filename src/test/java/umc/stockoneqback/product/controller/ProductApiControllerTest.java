package umc.stockoneqback.product.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.fixture.ProductFixture;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.exception.GlobalErrorCode;
import umc.stockoneqback.product.controller.dto.request.ProductRequest;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.service.response.GetRequiredInfoResponse;
import umc.stockoneqback.product.service.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.response.LoadProductResponse;
import umc.stockoneqback.product.service.response.SearchProductResponse;
import umc.stockoneqback.role.exception.StoreErrorCode;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
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
import static umc.stockoneqback.common.DocumentFormatGenerator.*;
import static umc.stockoneqback.fixture.ProductFixture.*;
import static umc.stockoneqback.fixture.TokenFixture.ACCESS_TOKEN;
import static umc.stockoneqback.fixture.TokenFixture.BEARER_TOKEN;

@DisplayName("Product [Controller Layer] -> ProductApiController 테스트")
public class ProductApiControllerTest extends ControllerTest {
    private static final Long ERROR_STORE_ID = Long.MAX_VALUE;
    private static final Long ERROR_PRODUCT_ID = Long.MAX_VALUE;
    private static final String ERROR_STORE_CONDITION = "고온";
    private static final String ERROR_SORT_CONDITION = "제품 위치";
    private static final String ERROR_SEARCH_CONDITION = "전챼";

    @Nested
    @DisplayName("공통 예외")
    class commonError {
        private static final String BASE_URL = "/api/product/count";
        private static final String STORE_CONDITION = "상온";
        private static final Long STORE_ID = 1L;

        @Test
        @DisplayName("권한이 없는 사용자가 Product API를 호출한 경우 API 호출에 실패한다")
        void throwExceptionByUnauthorizedUser() throws Exception {
            // given
            doThrow(BaseException.type(GlobalErrorCode.INVALID_USER))
                    .when(productService)
                    .getTotalProduct(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.INVALID_USER;
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
                                    "ProductApi/CommonError/Case1",
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
        @DisplayName("입력된 사용자가 입력된 가게 소속이 아닌 경우 API 호출에 실패한다")
        void throwExceptionByConflictUserAndStore() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.USER_STORE_MATCH_FAIL))
                    .when(productService)
                    .getTotalProduct(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
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
                                    "ProductApi/CommonError/Case2",
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
        @DisplayName("입력된 사용자가 유효하지 않은 역할을 가지고 있는 경우 API 호출에 실패한다")
        void throwExceptionByInvalidUser() throws Exception {
            // given
            doThrow(BaseException.type(UserErrorCode.ROLE_NOT_FOUND))
                    .when(productService)
                    .getTotalProduct(anyLong(), anyLong(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("store", String.valueOf(STORE_ID))
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
                                    "ProductApi/CommonError/Case3",
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
    }

    @Nested
    @DisplayName("메인 호출 시 사용자의 가게 정보 조회 API [GET /api/product]")
    class getStoreInfoById {
        private static final String BASE_URL = "/api/product";
        private static final Long USER_ID = 1L;
        private static final Long STORE_ID = 5L;

        @Test
        @DisplayName("메인 호출 시 사용자의 가게 정보 조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(getRequiredInfoResponse())
                    .when(productService)
                    .getRequiredInfo(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result.userId").value(USER_ID),
                            jsonPath("$.result.storeId").value(STORE_ID)
                    )
                    .andDo(
                            document(
                                    "ProductApi/GetStoreById/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                            fieldWithPath("result.storeId").type(JsonFieldType.NUMBER).description("가게 ID")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("제품 등록 API [POST /api/product/add]")
    class addProduct {
        private static final String BASE_URL = "/api/product/add";
        private static final String STORE_CONDITION = "상온";
        private static final Long STORE_ID = 1L;

        @Test
        @DisplayName("유효하지 않은 가게 ID를 입력받으면 제품 등록에 실패한다")
        void throwExceptionByInvalidStore() throws Exception {
            // given
            doThrow(BaseException.type(StoreErrorCode.STORE_NOT_FOUND))
                    .when(productService)
                    .saveProduct(anyLong(), anyLong(), anyString(), any(), any());

            // when
            final ProductRequest request = createEditProductRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("editProductRequest", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
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
                                    "ProductApi/Add/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일 이미지"),
                                            partWithName("editProductRequest").attributes(getInputDTOFormat()).description("생성할 제품 정보 DTO")
                                    ),
                                    requestPartFields(
                                            "editProductRequest",
                                            fieldWithPath("name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                            fieldWithPath("vendor").type(JsonFieldType.STRING).description("판매업체"),
                                            fieldWithPath("receivingDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("입고일"),
                                            fieldWithPath("expirationDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("유통기한"),
                                            fieldWithPath("location").type(JsonFieldType.STRING).description("재료위치").optional(),
                                            fieldWithPath("requireQuant").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("siteToOrder").type(JsonFieldType.STRING).description("발주사이트").optional(),
                                            fieldWithPath("orderFreq").type(JsonFieldType.NUMBER).description("발주 빈도")
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
        @DisplayName("유효하지 않은 보관 방법을 입력받으면 제품 등록에 실패한다")
        void throwExceptionByInvalidCondition() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_STORE_CONDITION))
                    .when(productService)
                    .saveProduct(anyLong(), anyLong(), anyString(), any(), any());

            // when
            final ProductRequest request = createEditProductRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("editProductRequest", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
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
                                    "ProductApi/Add/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일 이미지"),
                                            partWithName("editProductRequest").attributes(getInputDTOFormat()).description("생성할 제품 정보 DTO")
                                    ),
                                    requestPartFields(
                                            "editProductRequest",
                                            fieldWithPath("name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                            fieldWithPath("vendor").type(JsonFieldType.STRING).description("판매업체"),
                                            fieldWithPath("receivingDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("입고일"),
                                            fieldWithPath("expirationDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("유통기한"),
                                            fieldWithPath("location").type(JsonFieldType.STRING).description("재료위치").optional(),
                                            fieldWithPath("requireQuant").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("siteToOrder").type(JsonFieldType.STRING).description("발주사이트").optional(),
                                            fieldWithPath("orderFreq").type(JsonFieldType.NUMBER).description("발주 빈도")
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
        @DisplayName("제품 등록에 성공한다")
        void success() throws Exception {
            // given
            doReturn(1L)
                    .when(productService)
                    .saveProduct(anyLong(), anyLong(), anyString(), any(), any());

            // when
            final ProductRequest request = createEditProductRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("editProductRequest", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ProductApi/Add/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일 이미지"),
                                            partWithName("editProductRequest").attributes(getInputDTOFormat()).description("생성할 제품 정보 DTO")
                                    ),
                                    requestPartFields(
                                            "editProductRequest",
                                            fieldWithPath("name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                            fieldWithPath("vendor").type(JsonFieldType.STRING).description("판매업체"),
                                            fieldWithPath("receivingDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("입고일"),
                                            fieldWithPath("expirationDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("유통기한"),
                                            fieldWithPath("location").type(JsonFieldType.STRING).description("재료위치").optional(),
                                            fieldWithPath("requireQuant").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("siteToOrder").type(JsonFieldType.STRING).description("발주사이트").optional(),
                                            fieldWithPath("orderFreq").type(JsonFieldType.NUMBER).description("발주 빈도")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("제품 상세조회 API [GET /api/product/{productId}]")
    class getDetailProduct {
        private static final String BASE_URL = "/api/product/{productId}";
        private static final Long PRODUCT_ID = 1L;

        @Test
        @DisplayName("유효하지 않은 제품 ID를 입력받으면 제품 상세조회에 실패한다")
        void throwExceptionByInvalidProductId() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT))
                    .when(productService)
                    .loadProduct(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, ERROR_PRODUCT_ID)
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
                                    "ProductApi/GetDetail/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("productId").description("상세조회할 제품 ID")
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
        @DisplayName("제품 상세조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(loadProductResponse())
                    .when(productService)
                    .loadProduct(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, PRODUCT_ID)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result.id").exists(),
                            jsonPath("$.result.id").value(PRODUCT_ID),
                            jsonPath("$.result.name").exists(),
                            jsonPath("$.result.name").value(APPLE.getName())
                    )
                    .andDo(
                            document(
                                    "ProductApi/GetDetail/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("productId").description("상세조회할 제품 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지"),
                                            fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("제품 ID"),
                                            fieldWithPath("result.name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("result.price").type(JsonFieldType.NUMBER).description("가격"),
                                            fieldWithPath("result.vendor").type(JsonFieldType.STRING).description("판매업체"),
                                            fieldWithPath("result.image").type(JsonFieldType.ARRAY).description("제품 이미지").optional(),
                                            fieldWithPath("result.receivingDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("입고일"),
                                            fieldWithPath("result.expirationDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("유통기한"),
                                            fieldWithPath("result.location").type(JsonFieldType.STRING).description("제품 위치").optional(),
                                            fieldWithPath("result.requireQuant").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("result.stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("result.siteToOrder").type(JsonFieldType.STRING).description("발주사이트").optional(),
                                            fieldWithPath("result.orderFreq").type(JsonFieldType.NUMBER).description("발주 빈도")
                                    )
                            )
                    );
        }
    }

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
                    .when(productService)
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
                    .when(productService)
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
                    .when(productService)
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
    @DisplayName("제품 변경 API [PATCH /api/product/edit/{productId}]")
    class modifyProduct {
        private static final String BASE_URL = "/api/product/edit/{productId}";
        private static final Long PRODUCT_ID = 1L;

        @Test
        @DisplayName("유효하지 않은 제품 ID를 입력받으면 제품 변경에 실패한다")
        void throwExceptionByInvalidProductId() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT))
                    .when(productService)
                    .editProduct(anyLong(), anyLong(), any(), any());

            // when
            final ProductRequest request = createEditProductRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("editProductRequest", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, ERROR_PRODUCT_ID)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                            request.setMethod("PATCH");
                            return request;
                        }
                    })
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
                                    "ProductApi/Modify/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("productId").description("변경할 제품 ID")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일 이미지"),
                                            partWithName("editProductRequest").attributes(getInputDTOFormat()).description("생성할 제품 정보 DTO")
                                    ),
                                    requestPartFields(
                                            "editProductRequest",
                                            fieldWithPath("name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                            fieldWithPath("vendor").type(JsonFieldType.STRING).description("판매업체"),
                                            fieldWithPath("receivingDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("입고일"),
                                            fieldWithPath("expirationDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("유통기한"),
                                            fieldWithPath("location").type(JsonFieldType.STRING).description("재료위치").optional(),
                                            fieldWithPath("requireQuant").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("siteToOrder").type(JsonFieldType.STRING).description("발주사이트").optional(),
                                            fieldWithPath("orderFreq").type(JsonFieldType.NUMBER).description("발주 빈도")
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
        @DisplayName("제품 변경에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(productService)
                    .editProduct(anyLong(), anyLong(), any(), any());

            // when
            final ProductRequest request = createEditProductRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("editProductRequest", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, PRODUCT_ID)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .with(new RequestPostProcessor() {
                        @Override
                        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                            request.setMethod("PATCH");
                            return request;
                        }
                    })
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ProductApi/Modify/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("productId").description("변경할 제품 ID")
                                    ),
                                    requestParts(
                                            partWithName("image").attributes(getInputImageFormat()).description("파일 이미지(변경 없을 시 null)"),
                                            partWithName("editProductRequest").attributes(getInputDTOFormat()).description("생성할 제품 정보 DTO")
                                    ),
                                    requestPartFields(
                                            "editProductRequest",
                                            fieldWithPath("name").type(JsonFieldType.STRING).description("제품명"),
                                            fieldWithPath("price").type(JsonFieldType.NUMBER).description("가격"),
                                            fieldWithPath("vendor").type(JsonFieldType.STRING).description("판매업체"),
                                            fieldWithPath("receivingDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("입고일"),
                                            fieldWithPath("expirationDate").type(JsonFieldType.STRING).attributes(getDateFormat()).description("유통기한"),
                                            fieldWithPath("location").type(JsonFieldType.STRING).description("재료위치").optional(),
                                            fieldWithPath("requireQuant").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuant").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("siteToOrder").type(JsonFieldType.STRING).description("발주사이트").optional(),
                                            fieldWithPath("orderFreq").type(JsonFieldType.NUMBER).description("발주 빈도")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("제품 삭제 API [DELETE /api/product/delete/{productId}]")
    class eraseProduct {
        private static final String BASE_URL = "/api/product/delete/{productId}";
        private static final Long PRODUCT_ID = 1L;

        @Test
        @DisplayName("유효하지 않은 제품 ID를 입력받으면 제품 삭제에 실패한다")
        void throwExceptionByInvalidProductId() throws Exception {
            // given
            doThrow(BaseException.type(ProductErrorCode.NOT_FOUND_PRODUCT))
                    .when(productService)
                    .deleteProduct(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, ERROR_PRODUCT_ID)
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
                                    "ProductApi/Erase/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("productId").description("삭제할 제품 ID")
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
        @DisplayName("제품 삭제에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(productService)
                    .deleteProduct(anyLong(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, PRODUCT_ID)
                    .header(AUTHORIZATION, BEARER_TOKEN + " " + ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk()
                    )
                    .andDo(
                            document(
                                    "ProductApi/Erase/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("productId").description("삭제할 제품 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").type(JsonFieldType.STRING).description("커스텀 예외 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메시지")
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
                    .when(productService)
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
                    .when(productService)
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
                    .when(productService)
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
                    .when(productService)
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
                    .when(productService)
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
                    .when(productService)
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
                    .when(productService)
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
                    .when(productService)
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

    private ProductRequest createEditProductRequest() {
        return new ProductRequest(
                APPLE.getName(),
                APPLE.getPrice(),
                APPLE.getVendor(),
                APPLE.getReceivingDate(),
                APPLE.getExpirationDate(),
                APPLE.getLocation(),
                APPLE.getRequireQuant(),
                APPLE.getStockQuant(),
                APPLE.getSiteToOrder(),
                APPLE.getOrderFreq()
        );
    }

    private LoadProductResponse loadProductResponse() {
        return new LoadProductResponse(
                1L,
                APPLE.getName(),
                APPLE.getPrice(),
                APPLE.getVendor(),
                null,
                APPLE.getReceivingDate(),
                APPLE.getExpirationDate(),
                APPLE.getLocation(),
                APPLE.getRequireQuant(),
                APPLE.getStockQuant(),
                APPLE.getSiteToOrder(),
                APPLE.getOrderFreq()
        );
    }

    private List<SearchProductResponse> searchProductResponse() {
        List<SearchProductResponse> searchProductResponseList = new ArrayList<>();
        searchProductResponseList.add(new SearchProductResponse(4L, DURIAN.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(11L, BLUEBERRY.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(8L, CHERRY.getName(), null));
        return searchProductResponseList;
    }

    private List<GetTotalProductResponse> getTotalProductResponse() {
        List<GetTotalProductResponse> getTotalProductResponseList = new ArrayList<>();
        getTotalProductResponseList.add(new GetTotalProductResponse("Total", 16));
        getTotalProductResponseList.add(new GetTotalProductResponse("Pass", 2));
        getTotalProductResponseList.add(new GetTotalProductResponse("Close", 5));
        getTotalProductResponseList.add(new GetTotalProductResponse("Lack", 5));
        return getTotalProductResponseList;
    }

    private List<SearchProductResponse> searchProductPageResponse() {
        List<SearchProductResponse> searchProductResponseList = new ArrayList<>();
        searchProductResponseList.add(new SearchProductResponse(16L, PERSIMMON.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(15L, TANGERINE.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(4L, DURIAN.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(3L, MANGO.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(9L, MELON.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(2L, BANANA.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(17L, PEAR.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(13L, PEACH.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(11L, BLUEBERRY.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(1L, APPLE.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(10L, WATERMELON.getName(), null));
        searchProductResponseList.add(new SearchProductResponse(5L, ORANGE.getName(), null));
        return searchProductResponseList;
    }

    private GetRequiredInfoResponse getRequiredInfoResponse() {
        return new GetRequiredInfoResponse(1L, 5L);
    }
}
