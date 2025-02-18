package prography.assignment.controller.admin;

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
import org.springframework.test.web.servlet.MockMvc;
import prography.assignment.domain.user.User;
import prography.assignment.service.admin.AdminService;
import prography.assignment.web.admin.AdminController;
import prography.assignment.web.admin.dto.request.InitRequest;
import prography.assignment.web.admin.dto.response.UserResponse;
import prography.assignment.web.admin.dto.response.UsersResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static prography.assignment.web.common.ResponseStatus.OK;

@WebMvcTest(controllers = AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    @DisplayName("GET /health - 서버가 정상 동작 중이면 200 응답을 반환한다.")
    @Test
    void healthCheck() throws Exception {
        // when // then
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()));
    }

    @DisplayName("POST /init - 기존 데이터를 초기화하고 새로운 유저 데이터를 생성한다.")
    @Test
    void initialize() throws Exception {
        // given
        InitRequest request = new InitRequest(5, 30);

        doNothing().when(adminService).init(request);

        // when // then
        mockMvc.perform(post("/init")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()));
    }

    @DisplayName("GET /user - 페이징 파라미터에 따른 유저 목록을 반환한다.")
    @Test
    void getUsers() throws Exception {
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
        List<UserResponse> userList = List.of(
                UserResponse.from(user1),
                UserResponse.from(user2)
        );

        int totalElements = 10;
        int pageSize = 2;
        int totalPages = 5;
        Pageable pageable = PageRequest.of(1, pageSize);
        Page<UserResponse> page = new PageImpl<>(userList, pageable, totalElements);
        UsersResponse response = UsersResponse.from(page);

        given(adminService.getUsers(any(Pageable.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/user")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(OK.getCode()))
                .andExpect(jsonPath("$.message").value(OK.getMessage()))
                .andExpect(jsonPath("$.result.totalElements").value(totalElements))
                .andExpect(jsonPath("$.result.totalPages").value(totalPages))
                .andExpect(jsonPath("$.result.userList", hasSize(userList.size())))
                .andExpect(jsonPath("$.result.userList[0].id").value(user1.getId()));
    }
}