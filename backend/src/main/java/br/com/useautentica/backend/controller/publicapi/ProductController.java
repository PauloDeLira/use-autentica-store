package br.com.useautentica.backend.controller.publicapi;

import br.com.useautentica.backend.dto.product.ProductResponse;
import br.com.useautentica.backend.dto.product.ProductSummaryResponse;
import br.com.useautentica.backend.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductSummaryResponse> list() {
        return productService.findActiveForCatalog();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return productService.findActiveByIdForCatalog(id);
    }
}
