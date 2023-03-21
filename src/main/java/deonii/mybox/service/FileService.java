package deonii.mybox.service;

import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.FileResponseDTO;
import deonii.mybox.data.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public interface FileService {
    ResponseDTO uploadFile(FileRequestDTO fileRequestDTO, UUID folderUuid, UUID userUuid) throws IOException;

    ResponseDTO deleteFile(UUID folderUuid, UUID fileUuid, UUID userUuid);

    StreamingResponseBody downloadFile(UUID folderUuid, UUID fileUuid, UUID userUuid, HttpServletResponse response);
}
