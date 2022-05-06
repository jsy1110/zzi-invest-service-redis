package zzipay.investservice.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private ExceptionEnum error;
    private String extraMessage;

    public CustomException(ExceptionEnum exception) {
        this(exception, "");
    }

    public CustomException(ExceptionEnum exception, String extraMessage) {
        super(exception.getMessage());
        this.error = exception;
        this.extraMessage = extraMessage;
    }
}