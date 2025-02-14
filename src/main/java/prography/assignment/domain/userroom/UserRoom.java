package prography.assignment.domain.userroom;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import prography.assignment.domain.room.Room;
import prography.assignment.domain.user.User;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    private String team; // RED, BLUE
}
