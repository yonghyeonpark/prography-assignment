package prography.assignment.web.team;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import prography.assignment.service.room.RoomService;
import prography.assignment.web.ApiResponse;
import prography.assignment.web.team.dto.request.ChangeTeamRequest;

@RequiredArgsConstructor
@RestController
public class TeamController {

    private final RoomService roomService;

    @PutMapping("/team/{roomId}")
    public ApiResponse<Void> changeTeam(@PathVariable Integer roomId, @RequestBody ChangeTeamRequest changeTeamRequest) {
        roomService.changeTeam(roomId, changeTeamRequest);
        return ApiResponse.responseSuccess();
    }
}
