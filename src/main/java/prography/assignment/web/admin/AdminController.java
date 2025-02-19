package prography.assignment.web.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import prography.assignment.service.admin.AdminService;
import prography.assignment.web.admin.dto.request.InitRequest;
import prography.assignment.web.admin.dto.response.UsersResponse;
import prography.assignment.web.common.ApiResponse;

@Tag(name = "관리자")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "헬스 체크")
    @GetMapping("/health")
    public ApiResponse<Void> healthCheck() {
        return ApiResponse.responseSuccess();
    }

    @Operation(summary = "초기화")
    @PostMapping("/init")
    public ApiResponse<Void> init(@RequestBody InitRequest initRequest) {
        adminService.init(initRequest);
        return ApiResponse.responseSuccess();
    }

    @Operation(summary = "유저 목록 조회")
    @GetMapping("/user")
    public ApiResponse<UsersResponse> getUsers(
            @Parameter(description = "페이징 정보")
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.responseSuccess(adminService.getUsers(pageable));
    }
}
