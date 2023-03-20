package deonii.mybox.service.impl;

import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.error.CustomException;
import deonii.mybox.functions.UserFunctions;
import deonii.mybox.service.FolderService;
import deonii.mybox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FolderDAO folderDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserFunctions userFunctions;

    @Autowired
    private FolderService folderService;

    @Override
    public ResponseDTO signup(UserRequestDTO userRequestDTO,
                              HttpServletResponse response) {
        boolean isExistUser = userDAO.checkEmail(userRequestDTO.getEmail());

        if(isExistUser) {
            throw new CustomException(ALREADY_EXISTS_EMAIL);
        }

        // 요청 데이터를 통해 유저 저장
        UserEntity user = new UserEntity(userRequestDTO);
        user.encryptPassword(passwordEncoder);

        UserEntity saveUser = userDAO.saveUser(user);

        // 개인 유저의 폴더 생성(uuid를 폴더명으로 함)
        FolderEntity folderEntity = new FolderEntity(saveUser.getUuid().toString(), null, saveUser);
        folderService.createRootFolder(folderEntity);

        // 저장된 유저 데이터로 세션 생성 및 쿠키 생성
        SessionEntity saveSession = userFunctions.createSession(saveUser, response);

        HashMap<String, Object> body = new HashMap<>();
        body.put("expire_at", saveSession.getExpireAt());

        ResponseDTO responseDTO = new ResponseDTO(201, "Sign Up", saveSession.getCreateAt(), body);
        return responseDTO;
    }

    @Override
    public ResponseDTO login(UserRequestDTO userRequestDTO,
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

        userDAO.updateLastLogin(userEntity);

        userFunctions.deleteSession(request, response);
        SessionEntity session = userFunctions.createSession(userEntity, response);

        HashMap<String, Object> body = new HashMap<>();
        body.put("expire_at", session.getExpireAt());

        ResponseDTO responseDTO = new ResponseDTO(200, "Log In", session.getCreateAt(), body);

        return responseDTO;
    }

    @Override
    public ResponseDTO logout(HttpServletRequest request, HttpServletResponse response) {
        userFunctions.deleteSession(request, response);

        ResponseDTO responseDTO = new ResponseDTO(200, "Log Out", LocalDateTime.now(), null);
        return responseDTO;
    }

    @Override
    public ResponseDTO getUserInfo(UUID userUuid) {
        UserEntity userEntity = userDAO.findByUuid(userUuid);
        if(userEntity == null) {
            throw new CustomException(NOT_EXISTS_UUID);
        }
        FolderEntity rootFolderEntity = folderDAO.findByNameAndParentPath(userUuid.toString(), "");

        HashMap<String, Object> body = new HashMap<>();
        body.put("user_info", userEntity);
        body.put("root_folder_uuid", rootFolderEntity.getUuid());

        ResponseDTO responseDTO = new ResponseDTO(200, "SUCCESS", LocalDateTime.now(), body);
        return responseDTO;
    }
}
