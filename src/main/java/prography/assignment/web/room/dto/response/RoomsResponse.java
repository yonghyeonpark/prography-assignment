package prography.assignment.web.room.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomsResponse {

    private final int totalElements;
    private final int totalPages;
    private final List<RoomResponse> rooms;

    public static RoomsResponse from(Page<RoomResponse> result) {
        return new RoomsResponse(
                (int) result.getTotalElements(),
                result.getTotalPages(),
                result.getContent()
        );
    }
}
