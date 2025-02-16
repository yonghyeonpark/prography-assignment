package prography.assignment.domain.userroom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_BLUE;
import static prography.assignment.domain.userroom.UserRoomConstants.TEAM_RED;

@Transactional
@SpringBootTest
public class UserRoomTest {

    @Autowired
    private UserRoomRepository userRoomRepository;

    @DisplayName("레드팀 유저가 팀 변경 시, 블루팀이 된다.")
    @Test
    void changeTeam_WhenRedTeam_ThenBlueTeam() {
        // given
        UserRoom userRoom = userRoomRepository.findById(1).get();

        // when
        userRoom.changeTeam();

        // then
        assertThat(userRoom.getTeam()).isEqualTo(TEAM_BLUE);
    }

    @DisplayName("블루팀 유저가 팀 변경 시, 레드팀이 된다.")
    @Test
    void changeTeam_WhenBlueTeam_ThenRedTeam() {
        // given
        UserRoom userRoom = userRoomRepository.findById(2).get();

        // when
        userRoom.changeTeam();

        // then
        assertThat(userRoom.getTeam()).isEqualTo(TEAM_RED);
    }
}
