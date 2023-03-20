package deonii.mybox.service;

import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.entity.FolderEntity;

import java.util.UUID;

public interface FolderService {
    void createRootFolder(FolderEntity folderEntity);

    ResponseDTO createFolder(FolderRequestDTO folderRequestDTO, UUID parentUuid, UUID userUuid);

    ResponseDTO browseFolder(UUID folderUuid, UUID userUuid);
}
