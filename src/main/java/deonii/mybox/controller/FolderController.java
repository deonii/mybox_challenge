package deonii.mybox.controller;

import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.FolderResponseDTO;
import deonii.mybox.error.CustomException;
import deonii.mybox.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.NOT_AUTHORIZATION;

@RestController
public class FolderController {

    @Autowired
    private FolderService folderService;

    @PostMapping("/folder/{folderUuid}")
    public FolderResponseDTO createFolder(@PathVariable UUID folderUuid,
                                          @Valid @RequestBody FolderRequestDTO folderRequestDTO,
                                          HttpServletRequest request
                             ) {
        UUID userUuid = (UUID) request.getAttribute("userUuid");

        if(userUuid == null) {
            throw new CustomException(NOT_AUTHORIZATION);
        }

        FolderResponseDTO folderResponseDTO = folderService.createFolder(folderRequestDTO, folderUuid, userUuid);
        return folderResponseDTO;
    }

}
