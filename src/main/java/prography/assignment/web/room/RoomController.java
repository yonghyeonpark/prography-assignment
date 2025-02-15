package prography.assignment.web.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import prography.assignment.service.room.RoomService;
import prography.assignment.web.ApiResponse;
import prography.assignment.web.room.dto.request.AttendRoomRequest;
import prography.assignment.web.room.dto.request.CreateRoomRequest;
import prography.assignment.web.room.dto.request.OutRoomRequest;
import prography.assignment.web.room.dto.request.StartRoomRequest;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;

@RequiredArgsConstructor
@RequestMapping("/room")
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ApiResponse<Void> createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        roomService.createRoom(createRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @GetMapping
    public ApiResponse<RoomsResponse> getRooms(Pageable pageable) {
        return ApiResponse.responseSuccess(roomService.getRooms(pageable));
    }

    @GetMapping("/{roomId}")
    public ApiResponse<RoomResponse> getRoomById(@PathVariable Integer roomId) {
        return ApiResponse.responseSuccess(roomService.getRoomById(roomId));
    }

    @PostMapping("/attention/{roomId}")
    public ApiResponse<Void> attendRoom(@PathVariable Integer roomId, @RequestBody AttendRoomRequest attendRoomRequest) {
        roomService.attendRoom(roomId, attendRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @PostMapping("/out/{roomId}")
    public ApiResponse<Void> outRoom(@PathVariable Integer roomId, @RequestBody OutRoomRequest outRoomRequest) {
        roomService.outRoom(roomId, outRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @PutMapping("/start/{roomId}")
    public ApiResponse<Void> startRoom(@PathVariable Integer roomId, @RequestBody StartRoomRequest startRoomRequest) {
        roomService.startRoom(roomId, startRoomRequest);
        return ApiResponse.responseSuccess();
    }
}
