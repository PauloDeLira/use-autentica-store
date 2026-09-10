package br.com.useautentica.backend.dto.productvariant;

import br.com.useautentica.backend.dto.color.ColorResponse;
import br.com.useautentica.backend.dto.size.SizeResponse;

import java.util.UUID;

public record ProductVariantResponse(
        UUID id,
        UUID productId,
        String productName,
        String sku,
        SizeResponse size,
        ColorResponse color,
        int stockQuantity,
        boolean active,
        boolean available
) {
}
