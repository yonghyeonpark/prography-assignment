package prography.assignment.service.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prography.assignment.domain.room.Room;
import prography.assignment.domain.room.RoomConstants;
import prography.assignment.domain.room.RoomRepository;
import prography.assignment.domain.user.User;
import prography.assignment.domain.user.UserConstants;
import prography.assignment.domain.user.UserRepository;
import prography.assignment.domain.userroom.UserRoom;
import prography.assignment.domain.userroom.UserRoomConstants;
import prography.assignment.domain.userroom.UserRoomRepository;
import prography.assignment.exception.CommonException;
import prography.assignment.web.room.dto.request.AttendRoomRequest;
import prography.assignment.web.room.dto.request.CreateRoomRequest;
import prography.assignment.web.room.dto.request.OutRoomRequest;
import prography.assignment.web.room.dto.request.StartRoomRequest;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;
import prography.assignment.web.team.dto.request.ChangeTeamRequest;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RoomService {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;

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
                        UserRoomConstants.TEAM_RED
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

        Room room = roomRepository.findByIdWithLock(roomId)
                .orElseThrow(CommonException::new);

        // 방 상태 검증 (WAIT 상태에만 참가 가능)
        if (!room.getStatus().equals(RoomConstants.WAIT)) {
            throw new CommonException();
        }

        int redCount = userRoomRepository.countByRoomIdAndRoomRoomType(roomId, UserRoomConstants.TEAM_RED);
        int blueCount = userRoomRepository.countByRoomIdAndRoomRoomType(roomId, UserRoomConstants.TEAM_BLUE);
        int currentCount = redCount + blueCount;

        // 참가 방 정원 초과 여부 검증
        int maxCapacity = room.getMaxCapacity();
        if (maxCapacity <= currentCount) {
            throw new CommonException();
        }

        // 팀당 최대 인원
        int teamCapacity = maxCapacity / 2;

        String team = UserRoomConstants.TEAM_RED;
        if (redCount == teamCapacity) {
            team = UserRoomConstants.TEAM_BLUE;
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

        // 호스트인 유저만 시작 가능

        // 방 인원이 모두 차야만 가능

        // WAIT에서만 시작 가능

        // PROGRESS로 변경

        // 시작 1분 뒤 FINISh로 변경 => 비동기 처리
    }

    @Transactional
    public void changeTeam(Integer roomId, ChangeTeamRequest changeTeamRequest) {

        // 참가자만 팀 변경 가능

        // 각 팀 인원 검증

        // 방의 상태 검증 (WAIT) 일 때만 가능
    }
}
