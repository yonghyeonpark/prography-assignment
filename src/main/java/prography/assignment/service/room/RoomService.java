package prography.assignment.service.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prography.assignment.domain.room.Room;
import prography.assignment.domain.room.RoomConstants;
import prography.assignment.domain.room.RoomRepository;
import prography.assignment.domain.user.User;
import prography.assignment.domain.user.UserConstants;
import prography.assignment.domain.user.UserRepository;
import prography.assignment.domain.userroom.UserRoom;
import prography.assignment.domain.userroom.UserRoomRepository;
import prography.assignment.exception.CommonException;
import prography.assignment.service.room.schedule.RoomFinisher;
import prography.assignment.web.room.dto.request.*;
import prography.assignment.web.room.dto.response.RoomForListResponse;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;

import java.time.Instant;
import java.util.List;

import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_BLUE;
import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_RED;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomService {

    private static final int GAME_FINISH_DELAY_SECONDS = 60;

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final TaskScheduler taskScheduler;
    private final RoomFinisher roomFinisher;

    // 방 생성
    @Transactional
    public void createRoom(CreateRoomRequest createRoomRequest) {
        Integer userId = createRoomRequest.userId();
        User host = getActiveUserOrThrow(userId);
        validateUserNotInRoom(userId);

        Room room = createRoomRequest.toEntity(host);
        roomRepository.save(room);
        userRoomRepository.save(
                new UserRoom(host, room, TEAM_RED)
        );
    }

    // 방 목록 조회
    public RoomsResponse getRooms(Pageable pageable) {
        Page<RoomForListResponse> result = roomRepository.findAllWithHost(pageable)
                .map(RoomForListResponse::from);
        return RoomsResponse.from(result);
    }

    // 방 단건 조회
    public RoomResponse getRoomById(Integer roomId) {
        Room room = getRoomOrThrow(roomId);
        return RoomResponse.from(room);
    }

    // 방 참가
    @Transactional
    public void attendRoom(
            Integer roomId,
            AttendRoomRequest attendRoomRequest
    ) {
        Integer userId = attendRoomRequest.userId();
        User user = getActiveUserOrThrow(userId);
        validateUserNotInRoom(userId);

        Room room = getWaitingRoomOrThrow(roomId);

        List<Integer> teamCounts = getTeamCounts(roomId);
        int redCount = teamCounts.get(0);
        int currentCount = teamCounts.get(2);

        // 참가 방 정원 초과 여부 검증
        int maxCapacity = room.getMaxCapacity();
        if (maxCapacity <= currentCount) {
            throw new CommonException();
        }

        // 기본으로 배정되는 팀은 RED
        // RED 팀의 정원이 차면 BLUE 팀으로 배정
        int teamMaxCapacity = getTeamMaxCapacity(maxCapacity);
        String team = TEAM_RED;
        if (redCount == teamMaxCapacity) {
            team = TEAM_BLUE;
        }

        userRoomRepository.save(
                new UserRoom(user, room, team)
        );
    }

    // 방 퇴장
    @Transactional
    public void outRoom(
            Integer roomId,
            OutRoomRequest outRoomRequest
    ) {
        Integer userId = outRoomRequest.userId();
        validateUserExists(userId);

        UserRoom userRoom = getUserRoomOrThrow(roomId, userId);
        Room room = getWaitingRoomOrThrow(roomId);

        // 호스트가 나가면 모든 참가자 퇴장 및 방 상태를 FINISH로 변경
        if (userId.equals(room.getHost().getId())) {
            userRoomRepository.deleteByRoomId(roomId);
            room.finish();
            return;
        }

        // 호스트가 아닌 경우 해당 유저만 퇴장
        userRoomRepository.delete(userRoom);
    }

    // 게임 시작
    @Transactional
    public void startRoom(Integer roomId, StartRoomRequest startRoomRequest) {
        Room room = getWaitingRoomOrThrow(roomId);

        List<Integer> teamCounts = getTeamCounts(roomId);
        int redCount = teamCounts.get(0);
        int blueCount = teamCounts.get(1);
        int currentCount = teamCounts.get(2);
        int maxCapacity = room.getMaxCapacity();

        // 방 인원수 검증
        // 1. 정원이 가득찼는지 검증
        // 2. 각 팀 인원수가 같은지 검증
        if (maxCapacity != currentCount || redCount != blueCount) {
            throw new CommonException();
        }

        Integer userId = startRoomRequest.userId();
        validateUserExists(userId);

        // 호스트인 유저만 시작 가능
        if (!room.getHost().getId().equals(userId)) {
            throw new CommonException();
        }

        // 방 상태를 PROGRESS로 변경
        room.start();

        // 방 시작 후 1분 뒤 FINISH로 변경
        scheduleRoomFinish(roomId);
    }

    // 팀 변경
    @Transactional
    public void changeTeam(Integer roomId, ChangeTeamRequest changeTeamRequest) {
        Room room = getWaitingRoomOrThrow(roomId);

        Integer userId = changeTeamRequest.userId();
        validateUserExists(userId);

        UserRoom userRoom = getUserRoomOrThrow(roomId, userId);

        // 변경 대상 팀의 인원수 조회
        String currentTeam = userRoom.getTeam();
        String targetTeam = currentTeam.equals(TEAM_RED) ? TEAM_BLUE : TEAM_RED;
        int targetTeamCount = userRoomRepository.countByRoomIdAndTeam(roomId, targetTeam);

        int maxCapacity = room.getMaxCapacity();
        int teamMaxCapacity = getTeamMaxCapacity(maxCapacity);

        // 변경 대상 팀의 인원수 검증
        if (targetTeamCount == teamMaxCapacity) {
            throw new CommonException();
        }

        // 팀 변경
        userRoom.changeTeam();
    }

    // 유저 존재 여부 및 활성 상태 검증 후 객체 반환
    private User getActiveUserOrThrow(Integer userId) {
        User user = userRepository.findByIdOrThrow(userId);
        if (!user.getStatus().equals(UserConstants.ACTIVE)) {
            throw new CommonException();
        }
        return user;
    }

    // 유저 존재 여부 검증
    private void validateUserExists(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new CommonException();
        }
    }

    // 유저가 이미 참여한 방이 있는지 검증
    private void validateUserNotInRoom(Integer userId) {
        if (userRoomRepository.existsByRoomHostId(userId)) {
            throw new CommonException();
        }
    }

    // 유저가 특정 방에 속해 있는지 확인하고 반환
    private UserRoom getUserRoomOrThrow(Integer roomId, Integer userId) {
        return userRoomRepository.findByRoomIdAndUserIdOrThrow(roomId, userId);
    }

    // 방 존재 여부 및 대기 상태 검증 후 객체 반환
    private Room getWaitingRoomOrThrow(Integer roomId) {
        Room room = getRoomOrThrow(roomId);
        if (!room.getStatus().equals(RoomConstants.WAIT)) {
            throw new CommonException();
        }
        return room;
    }

    // 방 존재 여부 검증 후 객체 반환
    private Room getRoomOrThrow(Integer roomId) {
        return roomRepository.findByIdWithHostOrThrow(roomId);
    }

    // 해당 방의 팀별 및 총 인원수를 반환
    private List<Integer> getTeamCounts(Integer roomId) {
        int redCount = userRoomRepository.countByRoomIdAndTeam(roomId, TEAM_RED);
        int blueCount = userRoomRepository.countByRoomIdAndTeam(roomId, TEAM_BLUE);
        return List.of(redCount, blueCount, redCount + blueCount);
    }

    // 방의 팀별 최대 인원수 반환
    private int getTeamMaxCapacity(int maxCapacity) {
        return maxCapacity / 2;
    }

    // 1분 후에 방 상태를 FINISH로 변경하는 스케줄러 등록
    private void scheduleRoomFinish(Integer roomId) {
        taskScheduler.schedule(
                () -> {
                    try {
                        roomFinisher.finish(roomId);
                    } catch (Exception e) {
                        throw new CommonException();
                    }
                },
                Instant.now().plusSeconds(GAME_FINISH_DELAY_SECONDS)
        );
    }
}
