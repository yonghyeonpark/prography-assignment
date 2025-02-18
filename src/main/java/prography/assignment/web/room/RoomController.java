package prography.assignment.web.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import prography.assignment.service.room.RoomService;
import prography.assignment.web.common.ApiResponse;
import prography.assignment.web.room.dto.request.*;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;

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
    public ApiResponse<RoomsResponse> getRooms(Pageable pageable) {
        return ApiResponse.responseSuccess(roomService.getRooms(pageable));
    }

    @GetMapping("/room/{roomId}")
    public ApiResponse<RoomResponse> getRoomById(@PathVariable Integer roomId) {
        return ApiResponse.responseSuccess(roomService.getRoomById(roomId));
    }

    @PostMapping("/room/attention/{roomId}")
    public ApiResponse<Void> attendRoom(@PathVariable Integer roomId, @RequestBody AttendRoomRequest attendRoomRequest) {
        roomService.attendRoom(roomId, attendRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @PostMapping("/room/out/{roomId}")
    public ApiResponse<Void> outRoom(@PathVariable Integer roomId, @RequestBody OutRoomRequest outRoomRequest) {
        roomService.outRoom(roomId, outRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @PutMapping("/room/start/{roomId}")
    public ApiResponse<Void> startRoom(@PathVariable Integer roomId, @RequestBody StartRoomRequest startRoomRequest) {
        roomService.startRoom(roomId, startRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @PutMapping("/team/{roomId}")
    public ApiResponse<Void> changeTeam(@PathVariable Integer roomId, @RequestBody ChangeTeamRequest changeTeamRequest) {
        roomService.changeTeam(roomId, changeTeamRequest);
        return ApiResponse.responseSuccess();
    }
}
