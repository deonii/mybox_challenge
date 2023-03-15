package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.repository.SessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class SessionDAOImpl implements SessionDAO {

    private final SessionRepository sessionRepository;

    @Override
    public SessionEntity saveSession(SessionEntity session) {
        SessionEntity sessionEntity = sessionRepository.save(session);
        return sessionEntity;
    }

    @Override
    public void deleteSession(SessionEntity session) {
        sessionRepository.delete(session);
    }

    @Override
    public SessionEntity findBySessionId(UUID sessionId) {
        SessionEntity sessionEntity = sessionRepository.findBySessionId(sessionId);
        return sessionEntity;
    }


}
