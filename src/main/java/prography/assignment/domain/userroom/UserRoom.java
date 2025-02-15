package prography.assignment.domain.userroom;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prography.assignment.domain.room.Room;
import prography.assignment.domain.user.User;

@Getter
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

    public UserRoom(
            User user,
            Room room,
            String team
    ) {
        this.user = user;
        this.room = room;
        this.team = team;
    }

    public void changeTeam() {
        if (team.equals(UserRoomConstants.TEAM_RED)) {
            this.team = UserRoomConstants.TEAM_BLUE;
            return;
        }
        this.team = UserRoomConstants.TEAM_RED;
    }
}