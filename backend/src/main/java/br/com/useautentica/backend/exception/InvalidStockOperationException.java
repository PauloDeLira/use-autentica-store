package br.com.useautentica.backend.exception;

/**
 * Lançada quando uma operação resultaria em estoque negativo ou inconsistente.
 */
public class InvalidStockOperationException extends RuntimeException {

    private final String errorCode;

    public InvalidStockOperationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
