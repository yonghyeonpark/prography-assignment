package prography.assignment.service.room.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prography.assignment.domain.room.Room;
import prography.assignment.domain.room.RoomRepository;

@RequiredArgsConstructor
@Service
public class RoomFinisher {

    private final RoomRepository roomRepository;

    @Transactional
    public void finish(Integer roomId) {
        Room room = roomRepository.findByIdOrThrow(roomId);
        room.finish();
    }
}
