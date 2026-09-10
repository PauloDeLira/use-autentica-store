package br.com.useautentica.backend.dto.productvariant;

import java.util.UUID;

public record ProductVariantSummary(
        UUID id,
        String sizeName,
        String colorName,
        String colorHexCode,
        int stockQuantity,
        boolean available
) {
}
