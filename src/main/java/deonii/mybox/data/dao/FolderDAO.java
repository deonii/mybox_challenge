package deonii.mybox.data.dao;

import deonii.mybox.data.entity.FolderEntity;

import java.util.List;
import java.util.UUID;

public interface FolderDAO {
    FolderEntity createFolder(FolderEntity folderEntity);

    FolderEntity findByUuid(UUID uuid);

    FolderEntity findByNameAndParentPath(String name, String parentPath);

    boolean existsByNameAndParentPath(String name, String parentPath);

    List<FolderEntity> findByParentUuid(UUID folderUuid);

    void deleteFolder(FolderEntity folderEntity);
}
