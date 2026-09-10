package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.dto.productvariant.ProductVariantRequest;
import br.com.useautentica.backend.dto.productvariant.ProductVariantResponse;
import br.com.useautentica.backend.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products/{productId}/variants")
public class ProductVariantAdminController {

    private final ProductVariantService productVariantService;

    public ProductVariantAdminController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductVariantResponse create(@PathVariable UUID productId, @Valid @RequestBody ProductVariantRequest request) {
        return productVariantService.create(productId, request);
    }

    @PutMapping("/{variantId}")
    public ProductVariantResponse update(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        return productVariantService.update(productId, variantId, request);
    }

    @DeleteMapping("/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID productId, @PathVariable UUID variantId) {
        productVariantService.delete(productId, variantId);
    }
}
