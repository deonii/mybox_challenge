package deonii.mybox.service.impl;

import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.dto.UserResponseDTO;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.data.repository.UserRepository;
import deonii.mybox.error.CustomException;
import deonii.mybox.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.ALREADY_EXISTS_EMAIL;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO signup(UserRequestDTO userRequestDTO,
                                  HttpServletResponse response,
                                  String cookieName
                       ) {
        boolean isExistUser = userDAO.checkEmail(userRequestDTO.getEmail());

        if(isExistUser) {
            throw new CustomException(ALREADY_EXISTS_EMAIL);
        }

        // 요청 데이터를 통해 유저 저장
        UserEntity user = new UserEntity(userRequestDTO);
        user.encryptPassword(passwordEncoder);

        UserEntity saveUser = userDAO.saveUser(user);

        // 저장된 유저 데이터로 세션 생성 및 쿠키 생성
        SessionEntity sessionEntity = new SessionEntity(saveUser);
        SessionEntity saveSession = sessionDAO.saveSession(sessionEntity);

        Cookie mySessionCookie = new Cookie(cookieName, saveSession.getSessionId().toString());
        response.addCookie(mySessionCookie);

        UserResponseDTO userResponseDTO = new UserResponseDTO("Login Success", saveSession.getCreateAt(),saveSession.getExpireAt());
        return userResponseDTO;
    }
}
