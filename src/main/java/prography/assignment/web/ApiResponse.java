package prography.assignment.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final int OK = 200;
    private static final int IMPOSSIBLE = 201;
    private static final int SERVER_ERROR = 500;
    private static final String OK_MESSAGE = "API 요청이 성공했습니다.";
    private static final String IMPOSSIBLE_MESSAGE = "불가능한 요청입니다.";
    private static final String SERVER_ERROR_MESSAGE = "에러가 발생했습니다.";

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
        return new ApiResponse<T>(OK, OK_MESSAGE, result);
    }

    public static <T> ApiResponse<T> responseSuccess() {
        return new ApiResponse<T>(OK, OK_MESSAGE);
    }

    public static <T> ApiResponse<T> responseImpossible(T result) {
        return new ApiResponse<T>(IMPOSSIBLE, IMPOSSIBLE_MESSAGE, result);
    }

    public static <T> ApiResponse<T> responseImpossible() {
        return new ApiResponse<T>(IMPOSSIBLE, IMPOSSIBLE_MESSAGE);
    }

    public static <T> ApiResponse<T> responseServerError(T result) {
        return new ApiResponse<T>(SERVER_ERROR, SERVER_ERROR_MESSAGE, result);
    }

    public static <T> ApiResponse<T> responseServerError() {
        return new ApiResponse<T>(SERVER_ERROR, SERVER_ERROR_MESSAGE);
    }
}
