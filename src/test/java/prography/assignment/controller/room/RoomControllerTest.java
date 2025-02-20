package prography.assignment.controller.room;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import prography.assignment.domain.room.Room;
import prography.assignment.domain.user.User;
import prography.assignment.exception.CommonException;
import prography.assignment.service.room.RoomService;
import prography.assignment.web.room.RoomController;
import prography.assignment.web.room.dto.request.*;
import prography.assignment.web.room.dto.response.RoomForListResponse;
import prography.assignment.web.room.dto.response.RoomResponse;
import prography.assignment.web.room.dto.response.RoomsResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static prography.assignment.domain.room.RoomConstants.SINGLE;
import static prography.assignment.web.common.ResponseStatus.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @DisplayName("POST /room - 방 생성 성공 시, 200 응답을 반환한다.")
    @Test
    void createRoom_returnsOkResponse() throws Exception {
        // given
        CreateRoomRequest request = new CreateRoomRequest(1, "title", SINGLE);

        doNothing()
                .when(roomService)
                .createRoom(request);

        // when // then
        mockMvc.perform(post("/room")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()));
    }

    // CommonException 발생에 대한 응답 결과 테스트
    @DisplayName("CommonException 발생 시, 201 응답을 반환한다.")
    @Test
    void throwCommonException_returnsImpossibleResponse() throws Exception {
        // given
        CreateRoomRequest request = new CreateRoomRequest(1, "title", SINGLE);

        doThrow(new CommonException())
                .when(roomService)
                .createRoom(request);

        // when // then
        mockMvc.perform(post("/room")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(IMPOSSIBLE.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(IMPOSSIBLE.getMessage()));
    }

    // CommonException를 제외한 나머지 RuntimeException 예외 발생에 대한 응답 결과 테스트
    @DisplayName("예상치 못한 예외 발생 시, 500 응답을 반환한다.")
    @Test
    void throwUnexpectedException_returnsServerErrorResponse() throws Exception {
        // given
        CreateRoomRequest request = new CreateRoomRequest(1, "title", SINGLE);

        doThrow(new RuntimeException())
                .when(roomService)
                .createRoom(request);

        // when // then
        mockMvc.perform(post("/room")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(SERVER_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(SERVER_ERROR.getMessage()));
    }

    @DisplayName("GET /room - 페이징 파라미터에 따른 방 목록과 200 응답을 반환한다.")
    @Test
    void getRooms_returnsOkResponse() throws Exception {
        // given
        User user1 = new User(
                1,
                5,
                "user1",
                "user1@email.com",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        User user2 = new User(
                2,
                9,
                "user2",
                "user2@email.com",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Room room1 = new Room(
                1,
                user1,
                "title",
                SINGLE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Room room2 = new Room(
                2,
                user2,
                "title",
                SINGLE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        List<RoomForListResponse> roomList = List.of(
                RoomForListResponse.from(room1),
                RoomForListResponse.from(room2)
        );

        int totalElements = 10;
        int pageSize = 2;
        int totalPages = 5;
        Pageable pageable = PageRequest.of(1, pageSize);
        Page<RoomForListResponse> page = new PageImpl<>(roomList, pageable, totalElements);
        RoomsResponse response = RoomsResponse.from(page);

        given(roomService.getRooms(any(Pageable.class)))
                .willReturn(response);

        // when // then
        mockMvc.perform(get("/room")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()))
                .andExpect(jsonPath("$.result.totalElements").value(totalElements))
                .andExpect(jsonPath("$.result.totalPages").value(totalPages))
                .andExpect(jsonPath("$.result.roomList", hasSize(roomList.size())))
                .andExpect(jsonPath("$.result.roomList[0].id").value(room1.getId()));
    }

    @DisplayName("GET /room/{roomId} - 방 단건 정보와 200 응답을 반환한다.")
    @Test
    void getRoomById_returnsOkResponse() throws Exception {
        // given
        User user = new User(
                1,
                5,
                "user",
                "user@email.com",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Room room = new Room(
                1,
                user,
                "title",
                SINGLE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        int roomId = room.getId();
        given(roomService.getRoomById(roomId))
                .willReturn(RoomResponse.from(room));

        // when // then
        mockMvc.perform(get("/room/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()))
                .andExpect(jsonPath("$.result.id").value(roomId));
    }

    @DisplayName("POST /room/attention/{roomId} - 방 참가 성공 시, 200 응답을 반환한다.")
    @Test
    void attendRoom_returnsOkResponse() throws Exception {
        // given
        AttendRoomRequest request = new AttendRoomRequest(1);
        int roomId = 2;

        doNothing()
                .when(roomService)
                .attendRoom(roomId, request);

        // when // then
        mockMvc.perform(post("/room/attention/{roomId}", roomId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()));
    }


    @DisplayName("POST /room/out/{roomId} - 방 퇴장 성공 시, 200 응답을 반환한다.")
    @Test
    void outRoom_returnsOkResponse() throws Exception {
        // given
        OutRoomRequest request = new OutRoomRequest(1);
        int roomId = 1;

        doNothing()
                .when(roomService)
                .outRoom(roomId, request);

        // when // then
        mockMvc.perform(post("/room/out/{roomId}", roomId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()));
    }


    @DisplayName("PUT /room/start/{roomId} - 방 시작 성공 시, 200 응답을 반환한다.")
    @Test
    void startRoom_returnsOkResponse() throws Exception {
        // given
        StartRoomRequest request = new StartRoomRequest(1);
        int roomId = 1;

        doNothing()
                .when(roomService)
                .startRoom(roomId, request);

        // when // then
        mockMvc.perform(put("/room/start/{roomId}", roomId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()));
    }


    @DisplayName("PUT /team/{roomId} - 팀 변경 성공 시, 200 응답을 반환한다.")
    @Test
    void changeTeam_returnsOkResponse() throws Exception {
        // given
        ChangeTeamRequest request = new ChangeTeamRequest(1);
        int roomId = 1;

        doNothing()
                .when(roomService)
                .changeTeam(roomId, request);

        // when // then
        mockMvc.perform(put("/team/{roomId}", roomId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()));
    }
}
