package br.com.useautentica.backend.dto.size;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SizeRequest(
        @NotBlank @Size(max = 20) String name,
        @NotNull @Min(0) Integer displayOrder
) {
}
