package prography.assignment.web.room.dto.request;

import prography.assignment.domain.room.Room;
import prography.assignment.domain.user.User;

public record CreateRoomRequest(
        Integer userId,
        String title,
        String roomType
) {

    public Room toEntity(User host) {
        return new Room(
                host,
                title,
                roomType
        );
    }
}
