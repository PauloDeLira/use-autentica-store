package br.com.useautentica.backend.exception;

/**
 * Lançada quando uma operação viola uma regra de negócio
 * (ex: preço inválido, produto inativo sendo publicado).
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
