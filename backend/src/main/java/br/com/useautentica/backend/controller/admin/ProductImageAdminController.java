package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.dto.productimage.ProductImageResponse;
import br.com.useautentica.backend.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products/{productId}/images")
public class ProductImageAdminController {

    private final ProductImageService productImageService;

    public ProductImageAdminController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageResponse upload(
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "altText", required = false) String altText
    ) {
        return productImageService.upload(productId, file, altText);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID productId, @PathVariable UUID imageId) {
        productImageService.delete(productId, imageId);
    }
}
