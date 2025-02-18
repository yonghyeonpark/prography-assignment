package prography.assignment.web.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import static prography.assignment.web.common.ResponseStatus.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final Integer code;
    private final String message;
    private T result;

    private ApiResponse(Integer code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    private ApiResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResponse<T> responseSuccess(T result) {
        return new ApiResponse<T>(OK.getCode(), OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> responseSuccess() {
        return new ApiResponse<T>(OK.getCode(), OK.getMessage());
    }

    public static <T> ApiResponse<T> responseImpossible(T result) {
        return new ApiResponse<T>(IMPOSSIBLE.getCode(), IMPOSSIBLE.getMessage(), result);
    }

    public static <T> ApiResponse<T> responseImpossible() {
        return new ApiResponse<T>(IMPOSSIBLE.getCode(), IMPOSSIBLE.getMessage());
    }

    public static <T> ApiResponse<T> responseServerError(T result) {
        return new ApiResponse<T>(SERVER_ERROR.getCode(), SERVER_ERROR.getMessage(), result);
    }

    public static <T> ApiResponse<T> responseServerError() {
        return new ApiResponse<T>(SERVER_ERROR.getCode(), SERVER_ERROR.getMessage());
    }
}
