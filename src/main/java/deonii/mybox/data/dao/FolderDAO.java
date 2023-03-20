package deonii.mybox.data.dao;

import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public interface FolderDAO {
    FolderEntity createFolder(FolderEntity folderEntity);
    FolderEntity findByUuid(UUID uuid);
    boolean existsByNameAndParentPath(String name, String parentPath);

    List<FolderEntity> findByParentUuid(UUID folderUuid);
}
