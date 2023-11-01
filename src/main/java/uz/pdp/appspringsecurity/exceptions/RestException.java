package uz.pdp.appspringsecurity.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestException extends RuntimeException {

    private String message;

    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public RestException(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public RestException(String message) {
        this.message = message;
        this.status = status;
    }
}
