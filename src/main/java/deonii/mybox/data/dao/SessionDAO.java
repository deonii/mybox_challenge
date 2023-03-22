package deonii.mybox.data.dao;

import deonii.mybox.data.entity.SessionEntity;

import java.util.UUID;

public interface SessionDAO {
    SessionEntity saveSession(SessionEntity session);

    void deleteSession(SessionEntity session);

    SessionEntity findBySessionId(UUID sessionId);
}
