package deonii.mybox.service.impl;

import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.dao.SessionDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.error.CustomException;
import deonii.mybox.error.ErrorCode;
import deonii.mybox.service.FolderService;
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
import java.util.List;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class FolderServiceImplTest {

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Autowired
    UserService userService;

    @Autowired
    FolderService folderService;

    @Value("${session.cookie.name}")
    public String cookieName;

    @Autowired
    SessionDAO sessionDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    FolderDAO folderDAO;

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

        FolderEntity rootFolder = folderDAO.findByNameAndParentPath(userUuid.toString(), "");
        folderService.createFolder(new FolderRequestDTO("secondFolder"), rootFolder.getUuid(), userUuid);
    }

    @Test
    void createRootFolder() {
        // given
        UUID userUuid =(UUID) request.getAttribute("userUuid");
        UserEntity userEntity = userDAO.findByUuid(userUuid);
        FolderEntity folderEntity = new FolderEntity("rootFolder", null, userEntity);

        // when
        folderService.createRootFolder(folderEntity);
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");

        // then
        Assertions.assertThat(rootFolder).isNotNull();
        Assertions.assertThat(rootFolder.getOwner()).isEqualTo(userEntity);
        Assertions.assertThat(rootFolder.getName()).isEqualTo("rootFolder");
    }

    @Test
    void createFolderFail() {
        // given
        FolderRequestDTO folderRequestDTO = new FolderRequestDTO("folder1");
        UUID userUuid = (UUID) request.getAttribute("userUuid");
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath(userUuid.toString(), "");
        UUID parentUuid = rootFolder.getUuid();

        // when
        CustomException notExistsUserException = assertThrows(CustomException.class, () -> {
            folderService.createFolder(folderRequestDTO, parentUuid, UUID.randomUUID());
        });
        ErrorCode notExistsUserExceptionErrorCode = notExistsUserException.getErrorCode();

        CustomException notExistsFolderException = assertThrows(CustomException.class, () -> {
            folderService.createFolder(folderRequestDTO, UUID.randomUUID(), userUuid);
        });
        ErrorCode notExistsFolderExceptionErrorCode = notExistsFolderException.getErrorCode();

        // then
        Assertions.assertThat(notExistsUserExceptionErrorCode).isEqualTo(NOT_EXISTS_UUID);
        Assertions.assertThat(notExistsFolderExceptionErrorCode).isEqualTo(NOT_EXISTS_FOLDER);
    }

    @Test
    void createFolderSuccess() {
        // given
        FolderRequestDTO folderRequestDTO = new FolderRequestDTO("folder1");
        UUID userUuid = (UUID) request.getAttribute("userUuid");
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath(userUuid.toString(), "");
        UUID parentUuid = rootFolder.getUuid();

        // when
        ResponseDTO responseDTO = folderService.createFolder(folderRequestDTO, parentUuid, userUuid);

        // then
        Assertions.assertThat(responseDTO.getStatus()).isEqualTo(201);
        Assertions.assertThat(responseDTO.getMessage()).isEqualTo("SUCCESS");
        Assertions.assertThat(responseDTO.getBody().get("folder_path")).isNotNull();
    }

    @Test
    void browseFolderFail() {
        // given
        UUID userUuid = (UUID) request.getAttribute("userUuid");
        FolderEntity secondFolder = folderDAO.findByNameAndParentPath("secondFolder", userUuid.toString() + "/");

        // when
        CustomException notExistsUserException = assertThrows(CustomException.class, () -> {
            folderService.browseFolder(secondFolder.getUuid(), UUID.randomUUID());
        });
        ErrorCode notExistsUserExceptionErrorCode = notExistsUserException.getErrorCode();

        CustomException notExistsFolderException = assertThrows(CustomException.class, () -> {
            folderService.browseFolder(UUID.randomUUID(), userUuid);
        });
        ErrorCode notExistsFolderExceptionErrorCode = notExistsFolderException.getErrorCode();

        // then
        Assertions.assertThat(notExistsUserExceptionErrorCode).isEqualTo(NOT_EXISTS_UUID);
        Assertions.assertThat(notExistsFolderExceptionErrorCode).isEqualTo(NOT_EXISTS_FOLDER);
    }

    @Test
    void browseFolderSuccess() {
        // given
        UUID userUuid = (UUID) request.getAttribute("userUuid");
        FolderEntity secondFolder = folderDAO.findByNameAndParentPath("secondFolder", userUuid + "/");

        // when
        ResponseDTO responseDTO = folderService.browseFolder(secondFolder.getUuid(), userUuid);

        // then
        Assertions.assertThat(responseDTO.getStatus()).isEqualTo(200);
        Assertions.assertThat(responseDTO.getMessage()).isEqualTo("SUCCESS");
        Assertions.assertThat(responseDTO.getBody().get("folder_list")).isInstanceOf(List.class);
        Assertions.assertThat(responseDTO.getBody().get("file_list")).isInstanceOf(List.class);
    }

    @Test
    void deleteFolderFail() {
        // given
        UUID userUuid = (UUID) request.getAttribute("userUuid");
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath(userUuid.toString(), "");
        FolderEntity secondFolder = folderDAO.findByNameAndParentPath("secondFolder", userUuid + "/");

        // when
        CustomException notExistsUserException = assertThrows(CustomException.class, () -> {
            folderService.deleteFolder(secondFolder.getUuid(), UUID.randomUUID());
        });
        ErrorCode notExistsUserExceptionErrorCode = notExistsUserException.getErrorCode();

        CustomException notExistsFolderException = assertThrows(CustomException.class, () -> {
            folderService.deleteFolder(UUID.randomUUID(), userUuid);
        });
        ErrorCode notExistsFolderExceptionErrorCode = notExistsFolderException.getErrorCode();

        CustomException rootFolderException = assertThrows(CustomException.class, () -> {
            folderService.deleteFolder(rootFolder.getUuid(), userUuid);
        });
        ErrorCode rootFolderExceptionErrorCode = rootFolderException.getErrorCode();

        // then
        Assertions.assertThat(notExistsUserExceptionErrorCode).isEqualTo(NOT_EXISTS_UUID);
        Assertions.assertThat(notExistsFolderExceptionErrorCode).isEqualTo(NOT_EXISTS_FOLDER);
        Assertions.assertThat(rootFolderExceptionErrorCode).isEqualTo(CAN_NOT_ROOT_FOLDER);
    }

    @Test
    void deleteFolderSuccess() {
        // given
        UUID userUuid = (UUID) request.getAttribute("userUuid");
        FolderEntity secondFolder = folderDAO.findByNameAndParentPath("secondFolder", userUuid + "/");

        // when
        ResponseDTO responseDTO = folderService.deleteFolder(secondFolder.getUuid(), userUuid);

        // then
        Assertions.assertThat(responseDTO.getStatus()).isEqualTo(200);
        Assertions.assertThat(responseDTO.getMessage()).isEqualTo("DELETE");
        Assertions.assertThat(responseDTO.getBody()).isNull();
    }
}