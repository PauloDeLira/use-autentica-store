package br.com.useautentica.backend.exception;

/**
 * Lançada quando um recurso solicitado não é encontrado
 * (ex: produto, categoria ou variação inexistente).
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String errorCode;

    public ResourceNotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
