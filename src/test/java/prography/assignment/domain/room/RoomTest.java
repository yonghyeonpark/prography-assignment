package prography.assignment.domain.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import prography.assignment.data.TestDataFactory;
import prography.assignment.domain.user.User;
import prography.assignment.domain.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RoomTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        List<User> users = TestDataFactory.createUsers();
        userRepository.saveAll(users);

        List<Room> rooms = TestDataFactory.createRooms(users);
        roomRepository.saveAll(rooms);
    }

    @DisplayName("단식(SINGLE) 방의 최대 인원은 2명이다.")
    @Test
    void singleRoom_maxCapacity_shouldBeTwo() {
        // given
        Room room = roomRepository.findById(1).get();

        // when // then
        assertThat(room.getMaxCapacity()).isEqualTo(2);
    }

    @DisplayName("복식(DOUBLE) 방의 최대 인원은 4명이다.")
    @Test
    void doubleRoom_maxCapacity_shouldBeFOUR() {
        // given
        Room room = roomRepository.findById(3).get();

        // when // then
        assertThat(room.getMaxCapacity()).isEqualTo(4);
    }

    @DisplayName("게임 시작 시, 방의 상태가 진행중(PROGRESS)으로 변경된다.")
    @Test
    void startGame_shouldChangeStatus_toInProgress() {
        // given
        Room room = roomRepository.findById(1).get();

        // when
        room.start();

        // then
        assertThat(room.getStatus()).isEqualTo(RoomConstants.IN_PROGRESS);
    }

    @DisplayName("게임 종료 시, 방의 상태가 종료(FINISH)로 변경된다.")
    @Test
    void finishGame_shouldChangeStatus_toFinish() {
        // given
        Room room = roomRepository.findById(1).get();

        // when
        room.finish();

        // then
        assertThat(room.getStatus()).isEqualTo(RoomConstants.FINISHED);
    }
}
