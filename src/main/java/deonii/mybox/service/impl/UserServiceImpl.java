package deonii.mybox.service.impl;

import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.dto.UserResponseDTO;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.data.repository.UserRepository;
import deonii.mybox.error.CustomException;
import deonii.mybox.functions.UserFunctions;
import deonii.mybox.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

//    @Autowired
//    private SessionDAO sessionDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFunctions userFunctions;

//    @Value("${session.cookie.name}")
//    private String cookieName;

    @Override
    public UserResponseDTO signup(UserRequestDTO userRequestDTO,
                                  HttpServletResponse response) {
        boolean isExistUser = userDAO.checkEmail(userRequestDTO.getEmail());

        if(isExistUser) {
            throw new CustomException(ALREADY_EXISTS_EMAIL);
        }

        // 요청 데이터를 통해 유저 저장
        UserEntity user = new UserEntity(userRequestDTO);
        user.encryptPassword(passwordEncoder);

        UserEntity saveUser = userDAO.saveUser(user);

        // 저장된 유저 데이터로 세션 생성 및 쿠키 생성
        SessionEntity saveSession = userFunctions.createSession(saveUser, response);

        UserResponseDTO userResponseDTO = new UserResponseDTO("Sign Up", saveSession.getCreateAt(), saveSession.getExpireAt());
        return userResponseDTO;
    }

    @Override
    public UserResponseDTO login(UserRequestDTO userRequestDTO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        boolean isExistUser = userDAO.checkEmail(userRequestDTO.getEmail());
        if(!isExistUser) {
            throw new CustomException(NOT_EXISTS_EMAIL);
        }

        UserEntity userEntity = userDAO.findByEmail(userRequestDTO.getEmail());

        boolean isMatch = userEntity.matchPassword(passwordEncoder, userRequestDTO.getPassword());
        if(!isMatch) {
            throw new CustomException(NOT_CORRECT_PASSWORD);
        }

        userFunctions.deleteSession(request, response);
        SessionEntity session = userFunctions.createSession(userEntity, response);

        UserResponseDTO userResponseDTO = new UserResponseDTO("Log In", session.getCreateAt(), session.getExpireAt());

        return userResponseDTO;
    }

    @Override
    public UserResponseDTO logout(HttpServletRequest request, HttpServletResponse response) {
        userFunctions.deleteSession(request, response);
        UserResponseDTO userResponseDTO = new UserResponseDTO("Log Out", LocalDateTime.now(), LocalDateTime.now());
        return userResponseDTO;
    }

//    public SessionEntity createSession(UserEntity user, HttpServletResponse response) {
//        SessionEntity sessionEntity = new SessionEntity(user);
//        SessionEntity saveSession = sessionDAO.saveSession(sessionEntity);
//
//        Cookie mySessionCookie = new Cookie(cookieName, saveSession.getSessionId().toString());
//        mySessionCookie.setMaxAge(1000 * 60 * 60 * 24 * 7);
//        response.addCookie(mySessionCookie);
//        return saveSession;
//    }
//
//    public void deleteSession(HttpServletRequest request, HttpServletResponse response) {
//        Cookie mySessionCookie = findCookie(request);
//        if(mySessionCookie == null) return;
//        String stringSessionId = mySessionCookie.getValue();
//        UUID sessionId = UUID.fromString(stringSessionId);
//        SessionEntity sessionEntity = sessionDAO.findBySessionId(sessionId);
//        if(sessionEntity == null) return;
//        sessionDAO.deleteSession(sessionEntity);
//        expireCookie(response);
//    }
//
//    public Cookie findCookie(HttpServletRequest request) {
//        if(request.getCookies() == null) return null;
//        return Arrays.stream(request.getCookies())
//                .filter(cookie -> cookie.getName().equals(cookieName))
//                .findAny()
//                .orElse(null);
//    }
//
//    public void expireCookie(HttpServletResponse response) {
//        Cookie deleteCookie = new Cookie(cookieName, null);
//        deleteCookie.setMaxAge(0);
//        response.addCookie(deleteCookie);
//    }
}
