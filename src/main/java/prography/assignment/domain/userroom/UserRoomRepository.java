package prography.assignment.domain.userroom;

import org.springframework.data.jpa.repository.JpaRepository;
import prography.assignment.exception.CommonException;

import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {

    boolean existsByUserId(Integer userId);

    int countByRoomIdAndTeam(Integer roomId, String team);

    Optional<UserRoom> findByRoomIdAndUserId(Integer roomId, Integer userId);

    void deleteByRoomId(Integer roomId);

    default UserRoom findByRoomIdAndUserIdOrThrow(Integer roomId, Integer userId) {
        return findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(CommonException::new);
    }
}
