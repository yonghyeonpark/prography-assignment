package prography.assignment.client.dto.response;

import prography.assignment.domain.user.User;
import prography.assignment.domain.user.UserConstants;

public record FakerUser(Integer id, String username, String email) {

    public User toEntity() {
        return new User(
                id,
                username,
                email,
                setStatus()
        );
    }

    private String setStatus() {
        if (id <= 30) return UserConstants.ACTIVE;
        if (id <= 60) return UserConstants.WAIT;
        return UserConstants.INACTIVE;
    }
}
