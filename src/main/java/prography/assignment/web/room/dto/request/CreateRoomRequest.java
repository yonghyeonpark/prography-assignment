package prography.assignment.web.room.dto.request;

import prography.assignment.domain.room.Room;
import prography.assignment.domain.user.User;

import static prography.assignment.domain.room.RoomConstants.DOUBLE;
import static prography.assignment.domain.room.RoomConstants.SINGLE;

public record CreateRoomRequest(
        Integer userId,
        String title,
        String roomType
) {
    public CreateRoomRequest {
        if (!roomType.equals(SINGLE) && !roomType.equals(DOUBLE)) {
            throw new RuntimeException();
        }
    }

    public Room toEntity(User host) {
        return new Room(
                host,
                title,
                roomType
        );
    }
}