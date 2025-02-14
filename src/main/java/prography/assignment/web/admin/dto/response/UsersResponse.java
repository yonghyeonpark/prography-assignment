package prography.assignment.web.admin.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersResponse {

    private final int totalElements;
    private final int totalPages;
    private final List<UserResponse> users;

    public static UsersResponse from(Page<UserResponse> result) {
        return new UsersResponse(
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getContent()
        );
    }
}