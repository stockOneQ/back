package umc.stockoneqback.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.stockoneqback.global.annotation.ExtractPayload;
import umc.stockoneqback.global.base.BaseResponse;
import umc.stockoneqback.global.exception.GlobalErrorCode;
import umc.stockoneqback.product.dto.request.EditProductRequest;
import umc.stockoneqback.product.dto.response.GetRequiredInfoResponse;
import umc.stockoneqback.product.dto.response.LoadProductResponse;
import umc.stockoneqback.product.service.ProductService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductApiController {
    private final ProductService productService;

    @GetMapping("")
    public BaseResponse<GetRequiredInfoResponse> getRequiredInfo(@ExtractPayload Long userId) {
        return new BaseResponse<>(productService.getRequiredInfo(userId));
    }

    @PostMapping("/add")
    public BaseResponse<GlobalErrorCode> saveProduct(@ExtractPayload Long userId,
                                                     @RequestParam(value = "store") Long storeId,
                                                        @RequestParam(value = "condition") String storeConditionValue,
                                                        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                        @RequestPart(value = "editProductRequest") EditProductRequest editProductRequest) {
        productService.saveProduct(userId, storeId, storeConditionValue, editProductRequest.toProduct(), multipartFile);
        return new BaseResponse<>(GlobalErrorCode.CREATED);
    }

    @GetMapping("/{productId}")
    public BaseResponse<LoadProductResponse> loadProduct(@ExtractPayload Long userId,
                                                         @PathVariable(value = "productId") Long productId) throws IOException {
        return new BaseResponse<>(productService.loadProduct(userId, productId));
    }

    @PatchMapping("/edit/{productId}")
    public BaseResponse<GlobalErrorCode> editProduct(@ExtractPayload Long userId,
                                                     @PathVariable(value = "productId") Long productId,
                                                        @RequestPart(value = "image", required = false) MultipartFile multipartFile,
                                                        @RequestPart(value = "editProductRequest") EditProductRequest editProductRequest) {
        productService.editProduct(userId, productId, editProductRequest.toProduct(), multipartFile);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }

    @DeleteMapping("/delete/{productId}")
    public BaseResponse<GlobalErrorCode> deleteProduct(@ExtractPayload Long userId,
                                                       @PathVariable(value = "productId") Long productId) {
        productService.deleteProduct(userId, productId);
        return new BaseResponse<>(GlobalErrorCode.SUCCESS);
    }
}
