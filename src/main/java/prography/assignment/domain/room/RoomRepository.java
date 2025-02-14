package prography.assignment.domain.room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    // countQuery를 별도로 지정하지 않으면 불필요한 조인 발생
    @Query(
            value = """
                    SELECT r FROM Room r
                    JOIN FETCH r.host
                    """,
            countQuery = """
                    SELECT COUNT(r) FROM Room r
                    """
    )
    Page<Room> findAllWithHost(Pageable pageable);
}
