package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.dto.inventory.StockUpdateRequest;
import br.com.useautentica.backend.dto.productvariant.ProductVariantResponse;
import br.com.useautentica.backend.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryAdminController {

    private final ProductVariantService productVariantService;

    public InventoryAdminController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @GetMapping
    public List<ProductVariantResponse> list() {
        return productVariantService.findAllForInventory();
    }

    @PatchMapping("/{variantId}")
    public ProductVariantResponse updateStock(@PathVariable UUID variantId, @Valid @RequestBody StockUpdateRequest request) {
        return productVariantService.updateStock(variantId, request.stockQuantity());
    }
}
