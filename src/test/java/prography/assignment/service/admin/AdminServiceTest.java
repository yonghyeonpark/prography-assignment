package prography.assignment.service.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import prography.assignment.client.FakerClient;
import prography.assignment.client.dto.response.FakerResponse;
import prography.assignment.client.dto.response.FakerUser;
import prography.assignment.domain.room.RoomRepository;
import prography.assignment.domain.user.User;
import prography.assignment.domain.user.UserRepository;
import prography.assignment.domain.userroom.UserRoomRepository;
import prography.assignment.web.admin.dto.request.InitRequest;
import prography.assignment.web.admin.dto.response.UsersResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static prography.assignment.domain.user.UserConstants.*;

@Transactional
@SpringBootTest
public class AdminServiceTest {

    @MockBean
    private FakerClient fakerClient;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private RoomRepository roomRepository;

    @DisplayName("모든 데이터를 초기화하고 FakerUser를 통해 상태를 설정하여 유저 데이터를 저장한다.")
    @Test
    void initRequest_ThenClearAllDataAndSaveUsersFromFaker() {
        // given
        InitRequest request = new InitRequest(10, 5);

        FakerResponse mockResponse = new FakerResponse(
                List.of(
                        new FakerUser(30, "newUserA", "newUserA@email.com"),
                        new FakerUser(31, "newUserB", "newUserB@email.com"),
                        new FakerUser(60, "newUserC", "newUserC@email.com"),
                        new FakerUser(61, "newUserD", "newUserD@email.com")
                )
        );

        given(fakerClient.getData(anyInt(), anyInt()))
                .willReturn(mockResponse);

        // when
        adminService.init(request);

        // then
        // 데이터 초기화 검증
        assertThat(userRoomRepository.count()).isZero();
        assertThat(roomRepository.count()).isZero();

        // FakerUser의 ID에 따른 유저 상태 설정 검증
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(4);
        assertThat(users.get(0).getStatus()).isEqualTo(ACTIVE);
        assertThat(users.get(1).getStatus()).isEqualTo(WAIT);
        assertThat(users.get(2).getStatus()).isEqualTo(WAIT);
        assertThat(users.get(3).getStatus()).isEqualTo(NON_ACTIVE);
    }

    @DisplayName("유저 목록을 페이지네이션하여 올바른 데이터와 페이지 정보를 반환한다.")
    @Test
    void getUsers_WithPagination_ThenReturnUserList() {
        // given
        Pageable pageable = PageRequest.of(1, 2);

        // when
        UsersResponse users = adminService.getUsers(pageable);

        // then
        assertThat(users.getTotalElements()).isEqualTo(6);
        assertThat(users.getTotalPages()).isEqualTo(3);
        assertThat(users.getUserList())
                .hasSize(2)
                .extracting("name", "email")
                .containsExactly(
                        tuple("userC", "userC@email.com"),
                        tuple("userD", "userD@email.com")
                );
    }
}
