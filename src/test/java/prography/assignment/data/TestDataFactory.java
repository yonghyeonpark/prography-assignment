package prography.assignment.data;

import prography.assignment.domain.room.Room;
import prography.assignment.domain.user.User;

import java.util.List;

public class TestDataFactory {

    private TestDataFactory() {
    }

    public static List<User> createUsers() {
        return List.of(
                new User(1, "userA", "emailA", "ACTIVE"),
                new User(1, "userB", "emailB", "ACTIVE"),
                new User(1, "userC", "emailC", "ACTIVE"),
                new User(1, "userD", "emailD", "ACTIVE")
        );
    }

    public static List<Room> createRooms(List<User> users) {
        return List.of(
                new Room(users.get(0), "titleA", "SINGLE"),
                new Room(users.get(1), "titleB", "SINGLE"),
                new Room(users.get(2), "titleC", "DOUBLE"),
                new Room(users.get(3), "titleD", "DOUBLE")
        );
    }
}
