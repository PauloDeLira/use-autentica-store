package br.com.useautentica.backend.dto.product;

import br.com.useautentica.backend.dto.category.CategoryResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        boolean active,
        CategoryResponse category,
        Instant createdAt,
        Instant updatedAt
) {
}
