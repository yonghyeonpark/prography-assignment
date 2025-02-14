package prography.assignment.domain.room;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomConstants {

    // type
    public static final String SINGLE = "SINGLE"; // 단식
    public static final String DOUBLE = "DOUBLE"; // 복식

    // status
    public static final String WAIT = "WAIT"; // 대기
    public static final String IN_PROGRESS = "PROGRESS"; // 진행중
    public static final String FINISHED = "FINISH"; // 완료
}
