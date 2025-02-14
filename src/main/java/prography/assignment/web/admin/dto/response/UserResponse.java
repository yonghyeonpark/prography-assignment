package prography.assignment.web.admin.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import prography.assignment.domain.user.User;
import prography.assignment.util.DateTimeUtil;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    private final int id;
    private final int fakerId;
    private final String name;
    private final String email;
    private final String status;
    private final String createdAt;
    private final String updatedAt;


    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFakerId(),
                user.getName(),
                user.getEmail(),
                user.getStatus(),
                DateTimeUtil.formatDateTime(user.getCreatedAt()),
                DateTimeUtil.formatDateTime(user.getUpdatedAt())
        );
    }
}