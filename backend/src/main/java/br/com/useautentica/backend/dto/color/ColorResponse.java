package br.com.useautentica.backend.dto.color;

import java.util.UUID;

public record ColorResponse(
        UUID id,
        String name,
        String hexCode,
        boolean active
) {
}
