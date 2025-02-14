package prography.assignment.service.room;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prography.assignment.domain.room.Room;
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
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;

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
        Room room = roomRepository.findById(roomId)
                .orElseThrow(CommonException::new);
        return RoomResponse.from(room);
    }

    public void attendRoom(
            Integer roomId,
            AttendRoomRequest attendRoomRequest
    ) {
        User user = userRepository.findById(attendRoomRequest.userId())
                .orElseThrow(() -> new RuntimeException("dd"));

        // 유저 활성 상태인지 체크
        // 유저 참여한 방 있는지 검증


        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("dd"));

        // 방 대기 상태인지 체크
        // 참가 방 정원 체크 => 여기에 락 써야될듯

    }

    public void outOfRoom(Integer roomId) {
    }
}
