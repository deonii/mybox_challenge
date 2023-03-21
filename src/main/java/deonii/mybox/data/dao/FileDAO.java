package deonii.mybox.data.dao;

import deonii.mybox.data.entity.FileEntity;
import deonii.mybox.data.entity.SessionEntity;

import java.util.UUID;

public interface FileDAO {
    FileEntity saveFile(FileEntity fileEntity);

    boolean existsByNameAndFolderUuid(String name, UUID folderUuid);

    FileEntity findByUuid(UUID fileUuid);

    void deleteFile(FileEntity fileEntity);
}
