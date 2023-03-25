package deonii.mybox.service.impl;

import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.data.repository.SessionRepository;
import deonii.mybox.data.repository.UserRepository;
import deonii.mybox.error.CustomException;
import deonii.mybox.error.ErrorCode;
import deonii.mybox.service.UserService;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static deonii.mybox.error.ErrorCode.*;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Autowired
    UserService userService;

    @Value("${session.cookie.name}")
    public String cookieName;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    private SessionDAO sessionDAO;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii@test.com", "password");
        userService.signup(userRequestDTO, response);

        Cookie cookie = response.getCookie(cookieName);
        String stringSessionId = cookie.getValue();
        UUID sessionId = UUID.fromString(stringSessionId);
        SessionEntity sessionEntity = sessionDAO.findBySessionId(sessionId);
        UUID userUuid = sessionEntity.getUserUuid();
        request.setAttribute("userUuid", userUuid);
    }

    @Test
    void signupFail() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii@test.com", "password");

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.signup(userRequestDTO, response);
        });
        ErrorCode errorCode = exception.getErrorCode();

        // then
        Assertions.assertThat(errorCode).isEqualTo(ALREADY_EXISTS_EMAIL);
    }

    @Test
    void signupSuccess() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii_teste@test.com", "password");

        // when
        ResponseDTO responseDTO = userService.signup(userRequestDTO, response);
        Cookie cookie = response.getCookie(cookieName);

        // then
        Assertions.assertThat(responseDTO.getStatus()).isEqualTo(201);
        Assertions.assertThat(responseDTO.getMessage()).isEqualTo("Sign Up");
        Assertions.assertThat(responseDTO.getBody().get("expire_at")).isNotNull();
        Assertions.assertThat(cookie).isNotNull();
    }

    @Test
    void loginFail() {
        // given
        UserRequestDTO incorrectEmail = new UserRequestDTO("deonii_test@test.com", "password");
        UserRequestDTO incorrectPassword = new UserRequestDTO("deonii@test.com", "not this password");

        // given
        CustomException emailException = assertThrows(CustomException.class, () -> {
            userService.login(incorrectEmail, request, response);
        });
        ErrorCode emailErrorCode = emailException.getErrorCode();

        CustomException passwordException = assertThrows(CustomException.class, () ->{
            userService.login(incorrectPassword, request, response);
        });
        ErrorCode passwordErrorCode = passwordException.getErrorCode();

        // then
        Assertions.assertThat(emailErrorCode).isEqualTo(NOT_EXISTS_EMAIL);
        Assertions.assertThat(passwordErrorCode).isEqualTo(NOT_CORRECT_PASSWORD);
    }

    @Test
    void loginSuccess() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii@test.com", "password");

        // when
        ResponseDTO responseDTO = userService.login(userRequestDTO, request, response);
        Cookie cookie = response.getCookie(cookieName);

        // then
        Assertions.assertThat(responseDTO.getStatus()).isEqualTo(200);
        Assertions.assertThat(responseDTO.getMessage()).isEqualTo("Log In");
        Assertions.assertThat(responseDTO.getBody().get("expire_at")).isNotNull();
        Assertions.assertThat(cookie).isNotNull();
    }

    @Test
    void logout() {
        // when
        ResponseDTO responseDTO = userService.logout(request, response);

        // then
        Assertions.assertThat(responseDTO.getStatus()).isEqualTo(200);
        Assertions.assertThat(responseDTO.getMessage()).isEqualTo("Log Out");
        Assertions.assertThat(responseDTO.getBody()).isNull();
    }

    @Test
    void getUserInfoFail() {
        // given
        UUID randomUuid = UUID.randomUUID();

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.getUserInfo(randomUuid);
        });
        ErrorCode errorCode = exception.getErrorCode();

        // then
        Assertions.assertThat(errorCode).isEqualTo(NOT_EXISTS_UUID);
    }

    @Test
    void getUserInfoSuccess() {
        // given
        UUID userUuid = (UUID) request.getAttribute("userUuid");

        // when
        ResponseDTO responseDTO = userService.getUserInfo(userUuid);

        // then
        Assertions.assertThat(responseDTO.getStatus()).isEqualTo(200);
        Assertions.assertThat(responseDTO.getMessage()).isEqualTo("SUCCESS");
        Assertions.assertThat(responseDTO.getBody().get("user_info")).isInstanceOf(UserEntity.class);
        Assertions.assertThat(responseDTO.getBody().get("root_folder_uuid")).isInstanceOf(UUID.class);
    }
}