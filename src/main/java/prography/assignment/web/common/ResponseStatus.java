package prography.assignment.web.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {

    OK(200, "API 요청이 성공했습니다."),
    IMPOSSIBLE(201, "불가능한 요청입니다."),
    SERVER_ERROR(500, "에러가 발생했습니다.");

    private final Integer code;
    private final String message;
}
