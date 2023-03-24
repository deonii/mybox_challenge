package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;



@SpringBootTest
@Transactional
class FolderDAOImplTest {

    @Autowired
    FolderDAO folderDAO;

    @Autowired
    UserDAO userDAO;

    MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();

        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii@test.com", "password");
        UserEntity userEntity = new UserEntity(userRequestDTO);
        userDAO.saveUser(userEntity);
        FolderEntity folderEntity = new FolderEntity("rootFolder", null, userEntity);
        folderDAO.createFolder(folderEntity);
    }

    @Test
    void createFolder() {
        // given
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");
        FolderEntity folderEntity = new FolderEntity("testFolder", null, userEntity);

        // when
        FolderEntity savedFolderEntity = folderDAO.createFolder(folderEntity);

        // then
        Assertions.assertThat(savedFolderEntity).isNotNull();
        Assertions.assertThat(savedFolderEntity.getParentPath()).isEqualTo("");
        Assertions.assertThat(savedFolderEntity.getName()).isEqualTo("testFolder");
        Assertions.assertThat(savedFolderEntity.getOwner()).isEqualTo(userEntity);
    }

    @Test
    void findByUuid() {
        // given
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");
        FolderEntity folderEntity = new FolderEntity("testFolder", null, userEntity);
        FolderEntity savedFolderEntity = folderDAO.createFolder(folderEntity);
        UUID folderUuid = savedFolderEntity.getUuid();

        // when
        FolderEntity findFolder = folderDAO.findByUuid(folderUuid);

        // then
        Assertions.assertThat(findFolder).isEqualTo(savedFolderEntity);
    }

    @Test
    void findByNameAndParentPath() {
        // when
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");

        // then
        Assertions.assertThat(rootFolder).isNotNull();
        Assertions.assertThat(rootFolder.getOwner()).isEqualTo(userEntity);
    }

    @Test
    void existsByNameAndParentPath() {
        // when
        boolean isExists = folderDAO.existsByNameAndParentPath("rootFolder", "");

        // then
        Assertions.assertThat(isExists).isTrue();
    }

    @Test
    void findByParentUuid() {
        // given
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");
        FolderEntity childrenFolder = new FolderEntity("childrenFolder", rootFolder, userEntity);
        FolderEntity savedChildrenFolder = folderDAO.createFolder(childrenFolder);

        // when
        List<FolderEntity> byParentUuid = folderDAO.findByParentUuid(rootFolder.getUuid());

        // then
        Assertions.assertThat(byParentUuid).contains(savedChildrenFolder);
    }

    @Test
    void deleteFolder() {
        // given
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");

        // when
        folderDAO.deleteFolder(rootFolder);
        FolderEntity deletedRootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");

        // then
        Assertions.assertThat(deletedRootFolder).isNull();
    }
}