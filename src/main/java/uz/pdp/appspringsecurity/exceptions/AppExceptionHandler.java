package uz.pdp.appspringsecurity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import uz.pdp.appspringsecurity.payload.ApiResult;
import uz.pdp.appspringsecurity.payload.ErrorData;

import java.util.List;

@RestControllerAdvice
public class AppExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<?> handle(RestException e) {
        return ResponseEntity.status(e.getStatus().value())
                .body(ApiResult.errorResponse(e.getMessage(), e.getStatus().value()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handle(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ApiResult.errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResult<List<ErrorData>>> handle(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .body(ApiResult.errorResponse(e.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

}
