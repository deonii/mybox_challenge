package deonii.mybox.service;

import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.FolderResponseDTO;
import deonii.mybox.data.entity.FolderEntity;

import java.util.UUID;

public interface FolderService {
    void createRootFolder(FolderEntity folderEntity);

    FolderResponseDTO createFolder(FolderRequestDTO folderRequestDTO, UUID parentUuid, UUID userUuid);
}
