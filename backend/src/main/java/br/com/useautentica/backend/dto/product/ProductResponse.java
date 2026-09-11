package br.com.useautentica.backend.dto.product;

import br.com.useautentica.backend.dto.category.CategoryResponse;
import br.com.useautentica.backend.dto.productimage.ProductImageResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        boolean active,
        CategoryResponse category,
        List<ProductImageResponse> images,
        Instant createdAt,
        Instant updatedAt
) {
}
