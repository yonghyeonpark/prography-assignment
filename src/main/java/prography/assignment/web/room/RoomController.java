package prography.assignment.web.room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import prography.assignment.service.room.RoomService;
import prography.assignment.web.common.ApiResponse;
import prography.assignment.web.room.dto.request.*;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;

@Tag(name = "방")
@RequiredArgsConstructor
@RestController
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 생성")
    @PostMapping("/room")
    public ApiResponse<Void> createRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        roomService.createRoom(createRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @Operation(summary = "방 목록 조회")
    @GetMapping("/room")
    public ApiResponse<RoomsResponse> getRooms(
            @Parameter(description = "페이징 정보")
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.responseSuccess(roomService.getRooms(pageable));
    }

    @Operation(summary = "방 단건 조회")
    @GetMapping("/room/{roomId}")
    public ApiResponse<RoomResponse> getRoomById(@PathVariable Integer roomId) {
        return ApiResponse.responseSuccess(roomService.getRoomById(roomId));
    }

    @Operation(summary = "방 참가")
    @PostMapping("/room/attention/{roomId}")
    public ApiResponse<Void> attendRoom(@PathVariable Integer roomId, @RequestBody AttendRoomRequest attendRoomRequest) {
        roomService.attendRoom(roomId, attendRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @Operation(summary = "방 퇴장")
    @PostMapping("/room/out/{roomId}")
    public ApiResponse<Void> outRoom(@PathVariable Integer roomId, @RequestBody OutRoomRequest outRoomRequest) {
        roomService.outRoom(roomId, outRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @Operation(summary = "게임 시작")
    @PutMapping("/room/start/{roomId}")
    public ApiResponse<Void> startRoom(@PathVariable Integer roomId, @RequestBody StartRoomRequest startRoomRequest) {
        roomService.startRoom(roomId, startRoomRequest);
        return ApiResponse.responseSuccess();
    }

    @Operation(summary = "팀 변경")
    @PutMapping("/team/{roomId}")
    public ApiResponse<Void> changeTeam(@PathVariable Integer roomId, @RequestBody ChangeTeamRequest changeTeamRequest) {
        roomService.changeTeam(roomId, changeTeamRequest);
        return ApiResponse.responseSuccess();
    }
}
