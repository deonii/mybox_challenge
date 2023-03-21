package deonii.mybox.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import deonii.mybox.config.S3Config;
import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.FileResponseDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.functions.UserFunctions;
import deonii.mybox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserFunctions userFunctions;

    @PostMapping("/folder/{folderUuid}/file")
    private ResponseDTO uploadFile(@PathVariable UUID folderUuid,
                                   @Valid FileRequestDTO fileRequestDTO,
                                   HttpServletRequest request
                                   ) throws IOException {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);

        ResponseDTO responseDTO = fileService.uploadFile(fileRequestDTO, folderUuid, userUuid);
        return responseDTO;
    }

    @DeleteMapping("/folder/{folderUuid}/file/{fileUuid}")
    private ResponseDTO deleteFile(@PathVariable UUID folderUuid,
                                   @PathVariable UUID fileUuid,
                                   HttpServletRequest request) {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);

        ResponseDTO responseDTO = fileService.deleteFile(folderUuid, fileUuid, userUuid);
        return responseDTO;
    }

}
