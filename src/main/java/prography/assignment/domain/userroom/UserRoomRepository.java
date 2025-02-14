package prography.assignment.domain.userroom;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {

    boolean existsByRoomHostId(Integer hostId);

    int countByRoomIdAndRoomRoomType(Integer roomId, String roomType);

    boolean existsByRoomIdAndUserId(Integer roomId, Integer userId);

    void deleteByRoomId(Integer roomId);

    void deleteByRoomIdAndUserId(Integer roomId, Integer userId);
}
