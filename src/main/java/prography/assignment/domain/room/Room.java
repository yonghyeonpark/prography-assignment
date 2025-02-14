package prography.assignment.domain.room;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prography.assignment.domain.BaseTimeEntity;
import prography.assignment.domain.user.User;
import prography.assignment.exception.CommonException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Room extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "host_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User host;

    private String title;
    private String roomType; // SINGLE, DOUBLE
    private String status; // WAIT, PROGRESS, FINISH

    public Room(
            User host,
            String title,
            String roomType
    ) {
        this.host = host;
        this.title = title;
        this.roomType = roomType;
        this.status = RoomConstants.WAIT;
    }

    public int getMaxCapacity() {
        if (roomType.equals(RoomConstants.SINGLE)) {
            return 2;
        }
        if (roomType.equals(RoomConstants.DOUBLE)) {
            return 4;
        }
        throw new CommonException();
    }

    public void finishRoom() {
        this.status = RoomConstants.FINISHED;
    }
}
