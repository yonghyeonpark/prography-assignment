package prography.assignment.domain.room;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static prography.assignment.domain.room.RoomConstants.FINISHED;
import static prography.assignment.domain.room.RoomConstants.IN_PROGRESS;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class RoomTest {

    @Autowired
    private RoomRepository roomRepository;

    @DisplayName("단식(SINGLE) 방의 최대 인원은 2명이다.")
    @Test
    void getMaxCapacity_WhenSingleRoom_ThenTwo() {
        // given
        Room room = roomRepository.findByIdOrThrow(1);

        // when // then
        assertThat(room.getMaxCapacity()).isEqualTo(2);
    }

    @DisplayName("복식(DOUBLE) 방의 최대 인원은 4명이다.")
    @Test
    void getMaxCapacity_WhenDoubleRoom_ThenFour() {
        // given
        Room room = roomRepository.findByIdOrThrow(3);

        // when // then
        assertThat(room.getMaxCapacity()).isEqualTo(4);
    }

    @DisplayName("게임 시작 시, 방의 상태가 진행중(PROGRESS)으로 변경된다.")
    @Test
    void startGame_ThenRoomStatusIsProgress() {
        // given
        Room room = roomRepository.findByIdOrThrow(1);

        // when
        room.start();

        // then
        assertThat(room.getStatus()).isEqualTo(IN_PROGRESS);
    }

    @DisplayName("게임 종료 시, 방의 상태가 종료(FINISH)로 변경된다.")
    @Test
    void finishGame_ThenRoomStatusIsFinish() {
        // given
        Room room = roomRepository.findByIdOrThrow(1);

        // when
        room.finish();

        // then
        assertThat(room.getStatus()).isEqualTo(FINISHED);
    }
}
