package deonii.mybox.service;

import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.entity.FolderEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public interface FolderService {
    void createRootFolder(FolderEntity folderEntity);

    ResponseDTO createFolder(FolderRequestDTO folderRequestDTO, UUID parentUuid, UUID userUuid);

    ResponseDTO browseFolder(UUID folderUuid, UUID userUuid);

    ResponseDTO deleteFolder(UUID folderUuid, UUID userUuid);

    void downloadFolder(UUID folderUuid, UUID userUuid, HttpServletResponse response) throws IOException, InterruptedException;
}
