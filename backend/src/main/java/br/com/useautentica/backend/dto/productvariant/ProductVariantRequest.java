package br.com.useautentica.backend.dto.productvariant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductVariantRequest(
        @NotNull UUID sizeId,
        @NotNull UUID colorId,
        @NotNull @Min(0) Integer stockQuantity
) {
}
