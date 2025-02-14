package prography.assignment.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prography.assignment.domain.room.RoomRepository;
import prography.assignment.domain.user.UserRepository;
import prography.assignment.domain.userroom.UserRoomRepository;
import prography.assignment.web.admin.dto.response.UserResponse;
import prography.assignment.web.admin.dto.response.UsersResponse;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;

    public void init() {
        userRoomRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

    }

    public UsersResponse getUsers(Pageable pageable) {
        Page<UserResponse> result = userRepository.findAll(pageable)
                .map(UserResponse::from);
        return UsersResponse.from(result);
    }
}
