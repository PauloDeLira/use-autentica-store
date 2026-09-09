package br.com.useautentica.backend.exception;

import java.time.Instant;
import java.util.List;

/**
 * Formato padronizado de resposta de erro da API.
 *
 * Exemplo:
 * {
 *   "timestamp": "2026-09-08T12:00:00Z",
 *   "status": 404,
 *   "error": "PRODUCT_NOT_FOUND",
 *   "message": "Produto não encontrado",
 *   "path": "/api/products/10"
 * }
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetail> fieldErrors
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(Instant.now(), status, error, message, path, null);
    }

    public ErrorResponse(int status, String error, String message, String path, List<FieldErrorDetail> fieldErrors) {
        this(Instant.now(), status, error, message, path, fieldErrors);
    }

    public record FieldErrorDetail(String field, String message) {
    }
}
