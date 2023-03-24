package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.FileDAO;
import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.entity.FileEntity;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;



@SpringBootTest
@Transactional
class FileDAOImplTest {

    @Autowired
    UserDAO userDAO;

    @Autowired
    FolderDAO folderDAO;

    @Autowired
    FileDAO fileDAO;

    @BeforeEach
    void setUp() {
        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii@test.com", "password");
        UserEntity userEntity = new UserEntity(userRequestDTO);
        UserEntity savedUser = userDAO.saveUser(userEntity);

        FolderEntity folderEntity = new FolderEntity("rootFolder", null, savedUser);
        FolderEntity savedFolder = folderDAO.createFolder(folderEntity);

        FileEntity fileEntity = new FileEntity("firstFile.txt", 1024L, "txt", "rootFolder/", savedFolder, savedUser);
        fileDAO.saveFile(fileEntity);
    }


    @Test
    void saveFile() {
        // given
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");
        FileEntity fileEntity = new FileEntity("fileName.txt", 1024L, "txt", "rootFolder/", rootFolder, userEntity);

        // when
        FileEntity savedFile = fileDAO.saveFile(fileEntity);

        // then
        Assertions.assertThat(savedFile).isNotNull();
        Assertions.assertThat(savedFile.getName()).isEqualTo("fileName.txt");
        Assertions.assertThat(savedFile.getSize()).isEqualTo(1024L);
        Assertions.assertThat(savedFile.getFolder()).isEqualTo(rootFolder);
        Assertions.assertThat(savedFile.getOwner()).isEqualTo(userEntity);
    }

    @Test
    void existsByNameAndFolderUuid() {
        // given
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");

        // when
        boolean isExists = fileDAO.existsByNameAndFolderUuid("firstFile.txt", rootFolder.getUuid());
        boolean isNotExists = fileDAO.existsByNameAndFolderUuid("fakeFile.txt", rootFolder.getUuid());

        // then
        Assertions.assertThat(isExists).isTrue();
        Assertions.assertThat(isNotExists).isFalse();
    }

    @Test
    void findByUuid() {
        // given
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");
        FileEntity fileEntity = new FileEntity("fileName.txt", 1024L, "txt", "rootFolder/", rootFolder, userEntity);
        FileEntity savedFile = fileDAO.saveFile(fileEntity);

        // when
        FileEntity byUuid = fileDAO.findByUuid(savedFile.getUuid());

        // then
        Assertions.assertThat(byUuid).isNotNull();
        Assertions.assertThat(byUuid.getOwner()).isEqualTo(userEntity);
        Assertions.assertThat(byUuid.getFolder()).isEqualTo(rootFolder);
        Assertions.assertThat(byUuid.getName()).isEqualTo("fileName.txt");
    }

    @Test
    void deleteFile() {
        // given
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");
        FileEntity fileEntity = new FileEntity("fileName.txt", 1024L, "txt", "rootFolder/", rootFolder, userEntity);
        FileEntity savedFile = fileDAO.saveFile(fileEntity);
        UUID savedFileUuid = savedFile.getUuid();

        // when
        fileDAO.deleteFile(savedFile);
        FileEntity byUuid = fileDAO.findByUuid(savedFileUuid);

        // then
        Assertions.assertThat(byUuid).isNull();
    }

    @Test
    void findFileByFolderUuid() {
        // given
        FolderEntity rootFolder = folderDAO.findByNameAndParentPath("rootFolder", "");
        UserEntity userEntity = userDAO.findByEmail("deonii@test.com");
        FileEntity fileEntity = new FileEntity("fileName.txt", 1024L, "txt", "rootFolder/", rootFolder, userEntity);
        FileEntity savedFile = fileDAO.saveFile(fileEntity);

        // when
        List<FileEntity> fileList = fileDAO.findFileByFolderUuid(rootFolder.getUuid());

        // then
        Assertions.assertThat(fileList).contains(savedFile);
    }
}