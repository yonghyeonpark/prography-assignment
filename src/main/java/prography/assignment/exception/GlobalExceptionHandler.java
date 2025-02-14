package prography.assignment.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import prography.assignment.web.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonException.class)
    public ApiResponse<Void> handleCommonException() {
        return ApiResponse.responseImpossible();
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleRuntimeException() {
        return ApiResponse.responseServerError();
    }
}
