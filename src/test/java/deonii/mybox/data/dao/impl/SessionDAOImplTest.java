package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import java.util.Arrays;
import java.util.UUID;


@SpringBootTest
@Transactional
class SessionDAOImplTest {

    @Autowired
    UserDAO userDAO;

    @Autowired
    SessionDAO sessionDAO;

    @Value("${session.cookie.name}")
    String cookieName;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii@test.com", "password");
        UserEntity userEntity = new UserEntity(userRequestDTO);
        UserEntity savedUser = userDAO.saveUser(userEntity);
        SessionEntity sessionEntity = new SessionEntity(savedUser);
        SessionEntity saveSession = sessionDAO.saveSession(sessionEntity);

        Cookie mySessionCookie = new Cookie(cookieName, saveSession.getSessionId().toString());
        request.setCookies(mySessionCookie);
    }

    @Test
    void saveSession() {
        // given
        UserEntity findUser = userDAO.findByEmail("deonii@test.com");
        SessionEntity sessionEntity = new SessionEntity(findUser);

        // when
        SessionEntity saveSession = sessionDAO.saveSession(sessionEntity);

        // then
        Assertions.assertThat(saveSession).isNotNull();
        Assertions.assertThat(saveSession.getUserUuid()).isEqualTo(findUser.getUuid());
    }

    @Test
    void findBySessionId() {
        // given
        Cookie[] cookies = request.getCookies();
        Cookie findCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
        String stringSessionId = findCookie.getValue();
        UUID sessionId = UUID.fromString(stringSessionId);
        UserEntity findUser = userDAO.findByEmail("deonii@test.com");

        // when
        SessionEntity sessionEntity = sessionDAO.findBySessionId(sessionId);

        // then
        Assertions.assertThat(sessionEntity).isNotNull();
        Assertions.assertThat(sessionEntity.getUserUuid()).isEqualTo(findUser.getUuid());
    }

    @Test
    void deleteSession() {
        // given
        Cookie[] cookies = request.getCookies();
        Cookie findCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
        String stringSessionId = findCookie.getValue();
        UUID sessionId = UUID.fromString(stringSessionId);

        // when
        SessionEntity sessionEntity = sessionDAO.findBySessionId(sessionId);
        sessionDAO.deleteSession(sessionEntity);
        SessionEntity afterDelete = sessionDAO.findBySessionId(sessionId);

        // then
        Assertions.assertThat(afterDelete).isNull();
    }


}