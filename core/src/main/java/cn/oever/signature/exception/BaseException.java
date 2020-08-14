package cn.oever.signature.exception;

public class BaseException extends RuntimeException {
    private String message;

    public BaseException(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
