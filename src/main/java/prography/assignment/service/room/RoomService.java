package prography.assignment.service.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
import prography.assignment.web.room.dto.request.AttendRoomRequest;
import prography.assignment.web.room.dto.request.CreateRoomRequest;
import prography.assignment.web.room.dto.request.OutRoomRequest;
import prography.assignment.web.room.dto.request.StartRoomRequest;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;
import prography.assignment.web.team.dto.request.ChangeTeamRequest;

import java.time.Instant;

import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_BLUE;
import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_RED;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomService {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final ThreadPoolTaskScheduler taskScheduler;

    // 방 생성
    @Transactional
    public void createRoom(CreateRoomRequest createRoomRequest) {
        User host = userRepository.findById(createRoomRequest.userId())
                .orElseThrow(CommonException::new);

        // 유저 상태 검증
        if (!host.getStatus().equals(UserConstants.ACTIVE)) {
            throw new CommonException();
        }

        // 참여한 방 존재 여부 검증
        if (userRoomRepository.existsByRoomHostId(host.getId())) {
            throw new CommonException();
        }

        Room room = createRoomRequest.toEntity(host);
        roomRepository.save(room);
        userRoomRepository.save(
                new UserRoom(
                        host,
                        room,
                        TEAM_RED
                )
        );
    }

    // 방 목록 조회
    public RoomsResponse getRooms(Pageable pageable) {
        Page<RoomResponse> result = roomRepository.findAllWithHost(pageable)
                .map(RoomResponse::from);
        return RoomsResponse.from(result);
    }

    // 방 단건 조회
    public RoomResponse getRoomById(Integer roomId) {
        Room room = roomRepository.findByIdWithHost(roomId)
                .orElseThrow(CommonException::new);
        return RoomResponse.from(room);
    }

    // 방 참가
    @Transactional
    public void attendRoom(
            Integer roomId,
            AttendRoomRequest attendRoomRequest
    ) {
        User user = userRepository.findById(attendRoomRequest.userId())
                .orElseThrow(CommonException::new);

        // 유저 상태 검증 (ACTIVE 상태만 참가 가능)
        if (!user.getStatus().equals(UserConstants.ACTIVE)) {
            throw new CommonException();
        }

        // 참여한 방 존재 여부 검증
        if (userRoomRepository.existsByRoomHostId(user.getId())) {
            throw new CommonException();
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(CommonException::new);

        // 방 상태 검증 (WAIT 상태에만 참가 가능)
        if (!room.getStatus().equals(RoomConstants.WAIT)) {
            throw new CommonException();
        }

        int redCount = userRoomRepository.countByRoomIdAndRoomRoomType(roomId, TEAM_RED);
        int blueCount = userRoomRepository.countByRoomIdAndRoomRoomType(roomId, TEAM_BLUE);
        int currentCount = redCount + blueCount;

        // 참가 방 정원 초과 여부 검증
        int maxCapacity = room.getMaxCapacity();
        if (maxCapacity <= currentCount) {
            throw new CommonException();
        }

        // 팀당 최대 인원
        int teamCapacity = getTeamCapacity(maxCapacity);

        String team = TEAM_RED;
        if (redCount == teamCapacity) {
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
        // 유저 존재 여부 확인
        User user = userRepository.findById(outRoomRequest.userId())
                .orElseThrow(CommonException::new);
        Integer userId = user.getId();

        // 참가 여부 확인
        UserRoom userRoom = userRoomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(CommonException::new);

        // 방 존재 여부 확인
        Room room = roomRepository.findByIdWithHost(roomId)
                .orElseThrow(CommonException::new);

        // 방 상태 확인
        if (!room.getStatus().equals(RoomConstants.WAIT)) {
            throw new CommonException();
        }

        // 호스트가 나가면 모두 퇴장 및 FINISH로 상태 변경
        if (userId.equals(room.getHost().getId())) {
            userRoomRepository.deleteByRoomId(roomId);
            room.finishRoom();
            return;
        }

        // 호스트가 아닌 경우 해당 유저만 퇴장
        userRoomRepository.delete(userRoom);
    }

    // 게임 시작
    @Transactional
    public void startRoom(Integer roomId, StartRoomRequest startRoomRequest) {
        // 방 존재 여부 확인
        Room room = roomRepository.findByIdWithHost(roomId)
                .orElseThrow(CommonException::new);

        // 방 상태가 WAIT일 때만 시작 가능
        if (!room.getStatus().equals(RoomConstants.WAIT)) {
            throw new CommonException();
        }

        int redCount = userRoomRepository.countByRoomIdAndRoomRoomType(roomId, TEAM_RED);
        int blueCount = userRoomRepository.countByRoomIdAndRoomRoomType(roomId, TEAM_BLUE);
        int currentCount = redCount + blueCount;
        int maxCapacity = room.getMaxCapacity();

        // 방 인원수 검증
        // 1. 정원이 가득찼는지 검증
        // 2. 각 팀 인원수가 같은지 검증
        if (maxCapacity != currentCount || redCount != blueCount) {
            throw new CommonException();
        }

        Integer userId = startRoomRequest.userId();

        // 유저 존재 여부 확인
        if (userRepository.existsById(userId)) {
            throw new CommonException();
        }

        // 호스트인 유저만 시작 가능
        if (!room.getHost().getId().equals(userId)) {
            throw new CommonException();
        }

        // 방 상태를 PROGRESS로 변경
        room.startRoom();

        // 시작후 1분 뒤 FINISH로 변경
        scheduleRoomFinish(roomId);
    }

    // 팀 변경
    @Transactional
    public void changeTeam(Integer roomId, ChangeTeamRequest changeTeamRequest) {
        // 방 존재 여부 검증
        Room room = roomRepository.findById(roomId)
                .orElseThrow(CommonException::new);

        // 방의 상태 검증 (WAIT) 일 때만 가능
        if (!room.getStatus().equals(RoomConstants.WAIT)) {
            throw new CommonException();
        }

        // 유저 존재 여부 검증
        User user = userRepository.findById(changeTeamRequest.userId())
                .orElseThrow(CommonException::new);

        // 참가 여부 검증
        UserRoom userRoom = userRoomRepository.findByRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(CommonException::new);

        // 변경 대상 팀의 인원수 조회
        String currentTeam = userRoom.getTeam();
        String targetTeam = currentTeam.equals(TEAM_RED) ? TEAM_BLUE : TEAM_RED;
        int targetTeamCount = userRoomRepository.countByRoomIdAndRoomRoomType(roomId, targetTeam);

        int maxCapacity = room.getMaxCapacity();
        int teamCapacity = getTeamCapacity(maxCapacity);

        // 변경 대상 팀의 인원수 검증
        if (targetTeamCount == teamCapacity) {
            throw new CommonException();
        }

        // 팀 변경
        userRoom.changeTeam();
    }

    private int getTeamCapacity(int maxCapacity) {
        return maxCapacity / 2;
    }

    private void scheduleRoomFinish(Integer roomId) {
        taskScheduler.schedule(
                () -> {
                    try {
                        finishRoom(roomId);
                    } catch (Exception e) {
                        throw new CommonException();
                    }
                },
                Instant.now().plusSeconds(60)
        );
    }

    @Transactional
    protected void finishRoom(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(CommonException::new);

        room.finishRoom();
    }
}
