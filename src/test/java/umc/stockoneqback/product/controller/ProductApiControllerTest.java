package umc.stockoneqback.product.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import umc.stockoneqback.common.ControllerTest;
import umc.stockoneqback.product.dto.request.EditProductRequest;
import umc.stockoneqback.product.dto.response.LoadProductResponse;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static umc.stockoneqback.fixture.ProductFixture.APPLE;

@DisplayName("Product [Controller Layer] -> ProductApiController 테스트")
public class ProductApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("제품 등록 API [POST /api/product/add]")
    class addProduct {
        private static final String BASE_URL = "/api/product/add";
        private static final String STORE_CONDITION = "상온";
        private static final Long STORE_ID = 1L;

        @Test
        @DisplayName("제품 등록에 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(productService)
                    .saveProduct(anyLong(), anyString(), any(), any());

            // when
            final EditProductRequest request = createEditProductRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("editProductRequest", null,
                    "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URL)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .param("store", String.valueOf(STORE_ID))
                    .param("condition", STORE_CONDITION)
                    .with(csrf().asHeader());

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
                                    requestParts(
                                            partWithName("image").description("파일 이미지"),
                                            partWithName("editProductRequest").description("생성할 제품 정보 DTO")
                                    ),
                                    requestPartFields(
                                            "editProductRequest",
                                            fieldWithPath("name").description("제품명"),
                                            fieldWithPath("price").description("가격"),
                                            fieldWithPath("vendor").description("판매업체"),
                                            fieldWithPath("receivingDate").description("입고일"),
                                            fieldWithPath("expirationDate").description("유통기한"),
                                            fieldWithPath("location").description("재료위치"),
                                            fieldWithPath("requireQuant").description("필수 수량"),
                                            fieldWithPath("stockQuant").description("재고 수량"),
                                            fieldWithPath("siteToOrder").description("발주사이트"),
                                            fieldWithPath("orderFreq").description("발주 빈도")
                                    ),
                                    requestParameters(
                                            parameterWithName("store").description("현재 가게 ID"),
                                            parameterWithName("condition").description("현재 설정된 보관방법")
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
        @DisplayName("제품 상세조회에 성공한다")
        void success() throws Exception {
            // given
            doReturn(loadProductResponse())
                    .when(productService)
                    .loadProduct(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, PRODUCT_ID)
                    .with(csrf().asHeader());

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
                                    "ProductApi/Add/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productId").description("상세조회할 제품 ID")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").description("요청 상태"),
                                            fieldWithPath("errorCode").description("에러 코드"),
                                            fieldWithPath("message").description("요청 결과 메시지"),
                                            fieldWithPath("result.id").description("제품 ID"),
                                            fieldWithPath("result.name").description("제품명"),
                                            fieldWithPath("result.price").description("가격"),
                                            fieldWithPath("result.vendor").description("판매업체"),
                                            fieldWithPath("result.image").description("제품 이미지"),
                                            fieldWithPath("result.receivingDate").description("입고일"),
                                            fieldWithPath("result.expirationDate").description("유통기한"),
                                            fieldWithPath("result.location").description("제품 위치"),
                                            fieldWithPath("result.requireQuant").description("필수 수량"),
                                            fieldWithPath("result.stockQuant").description("재고 수량"),
                                            fieldWithPath("result.siteToOrder").description("발주사이트"),
                                            fieldWithPath("result.orderFreq").description("발주 빈도")
                                    )
                            )
                    );
        }
    }

    private EditProductRequest createEditProductRequest() {
        return new EditProductRequest(
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
}
