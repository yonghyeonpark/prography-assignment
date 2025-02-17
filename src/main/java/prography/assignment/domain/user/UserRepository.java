package prography.assignment.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import prography.assignment.exception.CommonException;

public interface UserRepository extends JpaRepository<User, Integer> {

    default User findByIdOrThrow(Integer userId) {
        return findById(userId)
                .orElseThrow(CommonException::new);
    }
}
