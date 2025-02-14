package prography.assignment.domain.userroom;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {

    boolean existsByRoomHostId(int hostId);
}
