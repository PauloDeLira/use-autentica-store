package br.com.useautentica.backend.dto.productimage;

import java.util.UUID;

public record ProductImageResponse(
        UUID id,
        String url,
        String altText,
        int displayOrder
) {
}
