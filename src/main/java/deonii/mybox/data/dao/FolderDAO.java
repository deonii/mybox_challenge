package deonii.mybox.data.dao;

import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;

public interface FolderDAO {
    FolderEntity createFolder(FolderEntity folderEntity);
}
