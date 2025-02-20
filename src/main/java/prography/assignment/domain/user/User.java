package prography.assignment.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prography.assignment.domain.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer fakerId;
    private String name;
    private String email;
    private String status; // WAIT, ACTIVE, NON_ACTIVE

    public User(
            Integer fakerId,
            String name,
            String email,
            String status
    ) {
        this.fakerId = fakerId;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    // 테스트용 생성자
    public User(
            Integer id,
            Integer fakerId,
            String name,
            String email,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(createdAt, updatedAt);
        this.id = id;
        this.fakerId = fakerId;
        this.name = name;
        this.email = email;
        this.status = status;
    }
}
