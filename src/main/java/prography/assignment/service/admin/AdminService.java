package prography.assignment.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prography.assignment.client.FakerClient;
import prography.assignment.client.dto.response.FakerUser;
import prography.assignment.domain.room.RoomRepository;
import prography.assignment.domain.user.User;
import prography.assignment.domain.user.UserRepository;
import prography.assignment.domain.userroom.UserRoomRepository;
import prography.assignment.web.admin.dto.request.InitRequest;
import prography.assignment.web.admin.dto.response.UserResponse;
import prography.assignment.web.admin.dto.response.UsersResponse;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final FakerClient fakerClient;

    public void init(InitRequest initRequest) {
        userRoomRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        List<User> users = fakerClient.getData(initRequest.seed(), initRequest.quantity())
                .data()
                .stream()
                .sorted(Comparator.comparing(FakerUser::id))
                .map(FakerUser::toEntity)
                .toList();

        userRepository.saveAll(users);
    }

    public UsersResponse getUsers(Pageable pageable) {
        Page<UserResponse> result = userRepository.findAll(pageable)
                .map(UserResponse::from);
        return UsersResponse.from(result);
    }
}
