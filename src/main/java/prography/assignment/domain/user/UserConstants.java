package prography.assignment.domain.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConstants {

    // status
    public static final String WAIT = "WAIT"; // 대기
    public static final String ACTIVE = "ACTIVE"; // 활성
    public static final String INACTIVE = "NON_ACTIVE"; // 비활성
}
