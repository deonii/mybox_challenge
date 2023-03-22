package deonii.mybox.data.dao;

import deonii.mybox.data.entity.FileEntity;

import java.util.List;
import java.util.UUID;

public interface FileDAO {
    FileEntity saveFile(FileEntity fileEntity);

    boolean existsByNameAndFolderUuid(String name, UUID folderUuid);

    FileEntity findByUuid(UUID fileUuid);

    void deleteFile(FileEntity fileEntity);

    List<FileEntity> findFileByFolderUuid(UUID folderUuid);
}
