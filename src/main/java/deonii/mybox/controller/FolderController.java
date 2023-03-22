package deonii.mybox.controller;

import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.functions.UserFunctions;
import deonii.mybox.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserFunctions userFunctions;

    @PostMapping("/folder/{folderUuid}")
    public ResponseDTO createFolder(@PathVariable UUID folderUuid,
                                    @Valid @RequestBody FolderRequestDTO folderRequestDTO,
                                    HttpServletRequest request
                             ) {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);

        ResponseDTO responseDTO = folderService.createFolder(folderRequestDTO, folderUuid, userUuid);
        return responseDTO;
    }

    @GetMapping("/folder/{folderUuid}")
    public ResponseDTO browseFolder(@PathVariable UUID folderUuid,
                                          HttpServletRequest request) {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);

        ResponseDTO responseDTO = folderService.browseFolder(folderUuid, userUuid);
        return responseDTO;
    }

    @DeleteMapping("/folder/{folderUuid}")
    public ResponseDTO deleteFolder(@PathVariable UUID folderUuid,
                                    HttpServletRequest request) {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);
        ResponseDTO responseDTO = folderService.deleteFolder(folderUuid, userUuid);
        return responseDTO;
    }

    @GetMapping("/folder/{folderUuid}/zip")
    public void downloadFolder(@PathVariable UUID folderUuid,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException, InterruptedException {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);
        folderService.downloadFolder(folderUuid, userUuid, response);
    }
}
