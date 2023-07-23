package umc.stockoneqback.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.global.base.BaseResponseStatus;
import umc.stockoneqback.product.dto.request.EditProductRequest;
import umc.stockoneqback.product.dto.response.LoadProductResponse;
import umc.stockoneqback.product.dto.response.SearchProductResponse;
import umc.stockoneqback.product.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    public BaseResponse<BaseResponseStatus> saveProduct(@RequestParam(value = "store") Long storeId,
                                                        @RequestParam(value = "condition") String storeConditionValue,
                                                        @RequestBody EditProductRequest editProductRequest) {
        productService.saveProduct(storeId, storeConditionValue, editProductRequest.toProduct(), editProductRequest.image());
        return new BaseResponse<>(BaseResponseStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public BaseResponse<LoadProductResponse> loadProduct(@PathVariable(value = "productId") Long productId) throws IOException {
        return new BaseResponse<>(productService.loadProduct(productId));
    }

    @GetMapping("")
    public BaseResponse<List<SearchProductResponse>> searchProduct(@RequestParam(value = "store") Long storeId,
                                                                   @RequestParam(value = "condition") String storeConditionValue,
                                                                   @RequestParam(value = "name") String productName) throws IOException {
        return new BaseResponse<>(productService.searchProduct(storeId, storeConditionValue, productName));
    }

    @PatchMapping("/edit/{productId}")
    public BaseResponse<BaseResponseStatus> editProduct(@PathVariable(value = "productId") Long productId,
                                                        @RequestBody EditProductRequest editProductRequest) {
        productService.editProduct(productId, editProductRequest.toProduct(), editProductRequest.image());
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @DeleteMapping("/delete/{productId}")
    public BaseResponse<BaseResponseStatus> deleteProduct(@PathVariable(value = "productId") Long productId) {
        productService.deleteProduct(productId);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }
}
