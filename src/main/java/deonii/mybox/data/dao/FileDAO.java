package deonii.mybox.data.dao;

import deonii.mybox.data.entity.FileEntity;

import java.util.UUID;

public interface FileDAO {
    FileEntity saveFile(FileEntity fileEntity);

    boolean existsByNameAndFolderUuid(String name, UUID folderUuid);
}
