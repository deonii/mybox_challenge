package deonii.mybox.controller;

import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.error.CustomException;
import deonii.mybox.functions.UserFunctions;
import deonii.mybox.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.NOT_AUTHORIZATION;

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
}
