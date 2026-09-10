package br.com.useautentica.backend.dto.category;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        boolean active
) {
}
