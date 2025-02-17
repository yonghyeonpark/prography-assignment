package prography.assignment.service.room;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.transaction.annotation.Transactional;
import prography.assignment.domain.room.Room;
import prography.assignment.domain.room.RoomRepository;
import prography.assignment.domain.user.User;
import prography.assignment.domain.user.UserRepository;
import prography.assignment.domain.userroom.UserRoom;
import prography.assignment.domain.userroom.UserRoomRepository;
import prography.assignment.exception.CommonException;
import prography.assignment.web.room.dto.request.*;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.verify;
import static prography.assignment.domain.room.RoomConstants.*;
import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_BLUE;
import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_RED;

@Transactional
@SpringBootTest
public class RoomServiceTest {

    @MockBean
    private TaskScheduler taskScheduler;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @DisplayName("방 생성 시, 데이터가 정상적으로 저장된다.")
    @Test
    void createRoom_shouldSaveNormally() {
        // given
        int existingCount = (int) roomRepository.count();
        String title = "newTitle";
        String roomType = "SINGLE";
        CreateRoomRequest request = new CreateRoomRequest(6, title, roomType);

        // when
        roomService.createRoom(request);

        // then
        assertThat(roomRepository.count()).isEqualTo(existingCount + 1);
        assertThat(roomRepository.findByIdOrThrow(existingCount + 1))
                .extracting("title", "roomType")
                .containsExactly(title, roomType);
    }

    @DisplayName("방 생성 시, 존재하지 않는 유저 ID라면 예외가 발생한다.")
    @Test
    void createRoom_shouldThrowException_whenUserNotFound() {
        // given
        String title = "newTitle";
        String roomType = "SINGLE";
        CreateRoomRequest request = new CreateRoomRequest(10, title, roomType);

        // when // then
        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("방 생성 시, 유저의 상태가 ACTIVE가 아니라면 예외가 발생한다.")
    @Test
    void createRoom_shouldThrowException_whenUserNotActive() {
        // given
        String title = "newTitle";
        String roomType = "SINGLE";
        CreateRoomRequest request = new CreateRoomRequest(9, title, roomType);

        // when // then
        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("방 생성 시, 이미 다른 방에 참가 중인 유저라면 예외가 발생한다.")
    @Test
    void createRoom_shouldThrowException_whenUserAlreadyInAnotherRoom() {
        // given
        String title = "newTitle";
        String roomType = "SINGLE";
        CreateRoomRequest request = new CreateRoomRequest(1, title, roomType);

        // when // then
        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(CommonException.class);
    }


    @DisplayName("방 목록을 페이지네이션하여 올바른 데이터와 페이지 정보를 반환한다.")
    @Test
    void getRooms_WithPagination_ThenReturnUserList() {
        // given
        Pageable pageable = PageRequest.of(1, 2);

        // when
        RoomsResponse rooms = roomService.getRooms(pageable);

        // then
        assertThat(rooms.getTotalElements()).isEqualTo(4);
        assertThat(rooms.getTotalPages()).isEqualTo(2);
        assertThat(rooms.getRoomList())
                .hasSize(2)
                .extracting("title", "roomType")
                .containsExactly(
                        tuple("복식방1", "DOUBLE"),
                        tuple("복식방2", "DOUBLE")
                );
    }

    @DisplayName("방 ID로 단건 조회 시, 올바른 데이터를 반환한다.")
    @Test
    void getRoom_ThenReturnRoom() {
        // given
        Integer roomId = 1;

        // when
        RoomResponse room = roomService.getRoomById(roomId);

        // then
        assertThat(room)
                .extracting("title", "roomType")
                .containsExactly("단식방1", "SINGLE");
    }

    @DisplayName("유저가 방에 참가하면 UserRoom에 참가 정보가 저장된다.")
    @Test
    void attendRoom_shouldSaveUserRoomRelation() {
        // given
        Integer roomId = 1;
        Integer userId = 5;
        AttendRoomRequest attendRoomRequest = new AttendRoomRequest(userId);

        // when
        roomService.attendRoom(roomId, attendRoomRequest);

        // then
        UserRoom userRoom = userRoomRepository.findByRoomIdAndUserIdOrThrow(roomId, userId);

        assertThat(userRoom.getUser().getId()).isEqualTo(userId);
        assertThat(userRoom.getRoom().getId()).isEqualTo(roomId);
    }

    @DisplayName("방 참가 시, 존재하지 않는 방 ID라면 예외가 발생한다.")
    @Test
    void attendRoom_shouldThrowException_whenRoomNotFound() {
        // given
        Integer roomId = 5;
        Integer userId = 5;
        AttendRoomRequest attendRoomRequest = new AttendRoomRequest(userId);

        // when // then
        assertThatThrownBy(() -> roomService.attendRoom(roomId, attendRoomRequest))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("방 참가 시, 방의 상태가 WAIT이 아니라면 예외가 발생한다.")
    @Test
    void attendRoom_shouldThrowException_whenRoomStateIsNotWait() {
        // given
        Integer roomId = 1;
        Integer userId = 5;
        AttendRoomRequest attendRoomRequest = new AttendRoomRequest(userId);

        Room room = roomRepository.findByIdOrThrow(roomId);
        room.start();

        // when // then
        assertThatThrownBy(() -> roomService.attendRoom(roomId, attendRoomRequest))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("방 참가 시, 방의 정원이 모두 찼다면 예외가 발생한다.")
    @Test
    void attendRoom_shouldThrowException_whenRoomIsFull() {
        // given
        Integer roomId = 1;
        Integer existingUserId = 5;
        Integer newUserId = 6;
        AttendRoomRequest attendRoomRequest = new AttendRoomRequest(existingUserId);
        AttendRoomRequest newAttendRoomRequest = new AttendRoomRequest(newUserId);

        roomService.attendRoom(roomId, attendRoomRequest);

        // when // then
        assertThatThrownBy(() -> roomService.attendRoom(roomId, newAttendRoomRequest))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("호스트가 아닌 유저가 방을 나가면 해당 유저만 퇴장되고 방의 상태는 WAIT으로 유지된다.")
    @Test
    void outRoom_whenNormalUserExits_shouldKeepRoomWaitingAndRemoveOnlyUser() {
        // given
        Integer roomId = 1;
        Integer userId = 5;

        Room room = roomRepository.findByIdOrThrow(roomId);
        User user = userRepository.findByIdOrThrow(userId);
        userRoomRepository.save(
                new UserRoom(
                        user,
                        room,
                        TEAM_RED
                )
        );

        OutRoomRequest request = new OutRoomRequest(userId);

        // when
        roomService.outRoom(roomId, request);

        // then
        int redCount = userRoomRepository.countByRoomIdAndTeam(roomId, TEAM_RED);
        int blueCount = userRoomRepository.countByRoomIdAndTeam(roomId, TEAM_BLUE);

        assertThat(redCount + blueCount).isNotZero();
        assertThat(roomRepository.findByIdOrThrow(roomId).getStatus()).isEqualTo(WAIT);
    }

    @DisplayName("호스트가 방을 나가면 모든 참가자가 퇴장되고 방의 상태가 FINISh로 변경된다.")
    @Test
    void outRoom_whenHostExits_shouldRemoveAllUsersAndFinishRoom() {
        // given
        Integer roomId = 1;
        Integer userId = 1;
        OutRoomRequest request = new OutRoomRequest(userId);

        // when
        roomService.outRoom(roomId, request);

        // then
        int redCount = userRoomRepository.countByRoomIdAndTeam(roomId, TEAM_RED);
        int blueCount = userRoomRepository.countByRoomIdAndTeam(roomId, TEAM_BLUE);

        assertThat(redCount + blueCount).isZero();
        assertThat(roomRepository.findByIdOrThrow(roomId).getStatus()).isEqualTo(FINISHED);
    }

    @DisplayName("호스트가 게임을 시작하면 방의 상태는 PROGRESS로 변경되고, 1분이 지나면 FINISH로 변경된다.")
    @Test
    void startRoom_shouldChangeToProgressAndFinishAfterOneMinute() {
        // given
        Integer roomId = 1;
        Integer hostId = 1;
        StartRoomRequest request = new StartRoomRequest(hostId);

        Integer participantId = 5;
        User participant = userRepository.findByIdOrThrow(participantId);
        Room room = roomRepository.findByIdOrThrow(roomId);
        userRoomRepository.save(
                new UserRoom(
                        participant,
                        room,
                        TEAM_BLUE
                )
        );

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);

        // when
        roomService.startRoom(roomId, request);

        // then
        // 시작 직후 방 상태 검증
        assertThat(room.getStatus()).isEqualTo(IN_PROGRESS);

        // taskScheduler.schedule() 호출 여부 검증 및 작업과 시간 캡쳐
        verify(taskScheduler).schedule(
                runnableCaptor.capture(),
                instantCaptor.capture()
        );

        // 캡쳐한 예약 시간 검증
        Instant scheduledTime = instantCaptor.getValue();
        assertThat(scheduledTime).isBetween(
                Instant.now().plusSeconds(59),
                Instant.now().plusSeconds(61)
        );

        // 캡쳐한 작업 실행
        runnableCaptor.getValue().run();

        // 시작 1분 후 방 상태 검증
        assertThat(room.getStatus()).isEqualTo(FINISHED);
    }

    @DisplayName("게임 시작 시, 정원이 가득차지 않은 상태라면 예외가 발생한다.")
    @Test
    void startRoom_shouldThrowException_whenRoomIsNotFull() {
        // given
        Integer roomId = 1;
        Integer hostId = 1;
        StartRoomRequest request = new StartRoomRequest(hostId);

        // when // then
        assertThatThrownBy(() -> roomService.startRoom(roomId, request))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("게임 시작 시, 팀별 인원수가 맞지 않다면 예외가 발생한다.")
    @Test
    void startRoom_shouldThrowException_whenTeamSizeIsNotEqual() {
        // given
        Integer roomId = 1;
        Integer hostId = 1;
        StartRoomRequest request = new StartRoomRequest(hostId);

        Integer participantId = 5;
        User participant = userRepository.findByIdOrThrow(participantId);
        Room room = roomRepository.findByIdOrThrow(roomId);
        userRoomRepository.save(
                new UserRoom(
                        participant,
                        room,
                        TEAM_RED
                )
        );

        // when // then
        assertThatThrownBy(() -> roomService.startRoom(roomId, request))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("호스트가 아닌 유저가 게임 시작 시, 예외가 발생한다.")
    @Test
    void startRoom_shouldThrowException_whenUserIsNotHost() {
        // given
        Integer roomId = 1;
        Integer userId = 2;
        StartRoomRequest request = new StartRoomRequest(userId);

        Integer participantId = 5;
        User participant = userRepository.findByIdOrThrow(participantId);
        Room room = roomRepository.findByIdOrThrow(roomId);
        userRoomRepository.save(
                new UserRoom(
                        participant,
                        room,
                        TEAM_BLUE
                )
        );

        // when // then
        assertThatThrownBy(() -> roomService.startRoom(roomId, request))
                .isInstanceOf(CommonException.class);
    }

    @DisplayName("팀 변경 시, 반대편 팀으로 변경된다.")
    @Test
    void changeTeam_shouldSwitchToOppositeTeam() {
        // given
        Integer roomId = 1;
        Integer userId = 1;
        ChangeTeamRequest request = new ChangeTeamRequest(userId);

        UserRoom userRoom = userRoomRepository.findByRoomIdAndUserIdOrThrow(roomId, userId);

        // when
        roomService.changeTeam(roomId, request);

        // then
        assertThat(userRoom.getTeam()).isEqualTo(TEAM_BLUE);
    }

    @DisplayName("팀 변경 시, 변경 대상팀이 이미 가득 찼다면 예외가 발생한다.")
    @Test
    void changeTeam_shouldThrowException_whenTargetTeamIsFull() {
        // given
        Integer roomId = 1;
        Integer userId = 5;
        ChangeTeamRequest request = new ChangeTeamRequest(userId);

        Room room = roomRepository.findByIdOrThrow(roomId);
        User user = userRepository.findByIdOrThrow(userId);
        userRoomRepository.save(
                new UserRoom(
                        user,
                        room,
                        TEAM_BLUE
                )
        );

        // when // then
        assertThatThrownBy(() -> roomService.changeTeam(roomId, request))
                .isInstanceOf(CommonException.class);
    }
}