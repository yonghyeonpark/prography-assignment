package prography.assignment.web.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import prography.assignment.domain.room.Room;
import prography.assignment.service.room.RoomService;
import prography.assignment.web.ApiResponse;
import prography.assignment.web.room.dto.request.CreateRoomRequest;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public ApiResponse<Void> createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        roomService.createRoom(createRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @GetMapping("/room")
    public ApiResponse<List<Room>> getRooms(Pageable pageable) {

    }

    @GetMapping("/room/{roomId}")
    public ApiResponse<Room> getRoomById(@PathVariable String roomId) {


    }
}
