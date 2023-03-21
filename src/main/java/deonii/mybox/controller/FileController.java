package deonii.mybox.controller;

import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.functions.UserFunctions;
import deonii.mybox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserFunctions userFunctions;

    @PostMapping("/folder/{folderUuid}/file")
    public ResponseDTO uploadFile(@PathVariable UUID folderUuid,
                                   @Valid FileRequestDTO fileRequestDTO,
                                   HttpServletRequest request
                                   ) throws IOException {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);

        ResponseDTO responseDTO = fileService.uploadFile(fileRequestDTO, folderUuid, userUuid);
        return responseDTO;
    }

    @DeleteMapping("/folder/{folderUuid}/file/{fileUuid}")
    public ResponseDTO deleteFile(@PathVariable UUID folderUuid,
                                   @PathVariable UUID fileUuid,
                                   HttpServletRequest request) {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);

        ResponseDTO responseDTO = fileService.deleteFile(folderUuid, fileUuid, userUuid);
        return responseDTO;
    }

    @GetMapping("/folder/{folderUuid}/file/{fileUuid}")
    public StreamingResponseBody getFile(@PathVariable UUID folderUuid,
                                         @PathVariable UUID fileUuid,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);

        return fileService.downloadFile(folderUuid, fileUuid, userUuid, response);
    }

}
