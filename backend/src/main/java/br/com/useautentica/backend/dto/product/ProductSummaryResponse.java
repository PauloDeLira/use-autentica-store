package br.com.useautentica.backend.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID id,
        String name,
        BigDecimal price,
        boolean active,
        String categoryName,
        String coverImageUrl,
        boolean available
) {
}
