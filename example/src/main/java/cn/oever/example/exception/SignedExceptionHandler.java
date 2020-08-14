package cn.oever.example.exception;

import cn.oever.signature.exception.SignedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SignedExceptionHandler {

    @ExceptionHandler(SignedException.ReplayAttack.class)
    public String exceptionHandler(SignedException.ReplayAttack ex) {
        String msg = ex.getMessage();
        return "This request may be replay attack, " + msg;
    }

    @ExceptionHandler(SignedException.NullParam.class)
    public String exceptionHandler(SignedException.NullParam ex) {
        String msg = ex.getMessage();
        return "The param " + msg + " is null";
    }

    @ExceptionHandler(SignedException.AppIdInvalid.class)
    public String exceptionHandler(SignedException.AppIdInvalid ex) {
        String msg = ex.getMessage();
        return "The appId " + msg + " is invalid";
    }

    @ExceptionHandler(SignedException.SignatureError.class)
    public String exceptionHandler(SignedException.SignatureError ex) {
        String msg = ex.getMessage();
        return "Signature is error: " + msg;
    }

    @ExceptionHandler(SignedException.TimestampError.class)
    public String exceptionHandler(SignedException.TimestampError ex) {
        String msg = ex.getMessage();
        return "The time difference is too large: " + msg;
    }
}
