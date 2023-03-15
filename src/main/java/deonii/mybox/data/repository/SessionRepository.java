package deonii.mybox.data.repository;

import deonii.mybox.data.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    SessionEntity findBySessionId(UUID sessionId);
}
