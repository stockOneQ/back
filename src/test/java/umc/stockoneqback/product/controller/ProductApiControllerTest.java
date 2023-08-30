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
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.product.controller.dto.request.ProductRequest;
import umc.stockoneqback.product.exception.ProductErrorCode;
import umc.stockoneqback.product.service.dto.response.GetRequiredInfoResponse;
import umc.stockoneqback.product.service.dto.response.GetTotalProductResponse;
import umc.stockoneqback.product.service.dto.response.LoadProductResponse;
import umc.stockoneqback.product.service.dto.response.SearchProductResponse;
import umc.stockoneqback.role.exception.StoreErrorCode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
                                            fieldWithPath("requireQuantity").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량"),
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
                                            fieldWithPath("requireQuantity").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량"),
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
                                            fieldWithPath("requireQuantity").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량"),
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
                                            fieldWithPath("result.requireQuantity").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("result.stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량"),
                                            fieldWithPath("result.siteToOrder").type(JsonFieldType.STRING).description("발주사이트").optional(),
                                            fieldWithPath("result.orderFreq").type(JsonFieldType.NUMBER).description("발주 빈도")
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
                                            fieldWithPath("requireQuantity").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량"),
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
                                            fieldWithPath("requireQuantity").type(JsonFieldType.NUMBER).description("필수 수량"),
                                            fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER).description("재고 수량"),
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

    private ProductRequest createEditProductRequest() {
        return new ProductRequest(
                APPLE.getName(),
                APPLE.getPrice(),
                APPLE.getVendor(),
                APPLE.getReceivingDate(),
                APPLE.getExpirationDate(),
                APPLE.getLocation(),
                APPLE.getRequireQuantity(),
                APPLE.getStockQuantity(),
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
                APPLE.getRequireQuantity(),
                APPLE.getStockQuantity(),
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
