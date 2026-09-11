package br.com.useautentica.backend.controller.publicapi;

import br.com.useautentica.backend.dto.product.ProductResponse;
import br.com.useautentica.backend.dto.product.ProductSummaryResponse;
import br.com.useautentica.backend.dto.productvariant.ProductVariantSummary;
import br.com.useautentica.backend.service.ProductService;
import br.com.useautentica.backend.service.ProductVariantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;

    public ProductController(ProductService productService, ProductVariantService productVariantService) {
        this.productService = productService;
        this.productVariantService = productVariantService;
    }

    @GetMapping
    public List<ProductSummaryResponse> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID sizeId,
            @RequestParam(required = false) UUID colorId
    ) {
        return productService.findActiveForCatalog(categoryId, sizeId, colorId);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return productService.findActiveByIdForCatalog(id);
    }

    @GetMapping("/{id}/variants")
    public List<ProductVariantSummary> variants(@PathVariable UUID id) {
        productService.findActiveByIdForCatalog(id);
        return productVariantService.findActiveByProductForCatalog(id);
    }
}
