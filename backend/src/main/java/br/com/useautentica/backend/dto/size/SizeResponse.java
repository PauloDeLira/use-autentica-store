package br.com.useautentica.backend.dto.size;

import java.util.UUID;

public record SizeResponse(
        UUID id,
        String name,
        int displayOrder,
        boolean active
) {
}
