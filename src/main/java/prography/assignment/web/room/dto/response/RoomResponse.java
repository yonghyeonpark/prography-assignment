package prography.assignment.web.room.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import prography.assignment.domain.room.Room;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomResponse {

    private final int id;
    private final String title;
    private final int hostId;
    private final String roomType;
    private final String status;

    public static RoomResponse from(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getTitle(),
                room.getHost().getId(),
                room.getRoomType(),
                room.getStatus()
        );
    }
}
