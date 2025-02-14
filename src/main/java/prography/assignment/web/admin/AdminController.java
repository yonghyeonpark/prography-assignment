package prography.assignment.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import prography.assignment.service.admin.AdminService;
import prography.assignment.web.ApiResponse;
import prography.assignment.web.admin.dto.response.UsersResponse;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/health")
    public ApiResponse<Void> healthCheck() {
        return ApiResponse.responseSuccess();
    }

    /*@PostMapping("/init")
    public ApiResponse<Void> init() {
        adminService.init();
        return ApiResponse.responseSuccess();
    }*/

    @GetMapping("/user")
    public ApiResponse<UsersResponse> getUsers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.responseSuccess(adminService.getUsers(pageable));
    }
}
