package deonii.mybox.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import deonii.mybox.data.dao.FileDAO;
import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.FileResponseDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.entity.FileEntity;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.error.CustomException;
import deonii.mybox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.*;


@Service
public class FileServiceImpl implements FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Autowired
    private FolderDAO folderDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FileDAO fileDAO;

    @Override
    public ResponseDTO uploadFile(FileRequestDTO fileRequestDTO, UUID folderUuid, UUID userUuid) throws IOException {
        MultipartFile file = fileRequestDTO.getFile();
        String fileName = file.getOriginalFilename();
        Long fileSize = file.getSize()/1000;

        if(file.isEmpty()) {
            throw new CustomException(FILE_DOES_NOT_EXIST);
        }

        UserEntity userEntity = userDAO.findByUuid(userUuid);
        if(userEntity == null) {
            throw new CustomException(NOT_EXISTS_UUID);
        }

        if(userEntity.getExtraVolume() < fileSize) {
            throw new CustomException(NOT_ENOUGH_VOLUME);
        }

        FolderEntity folderEntity = folderDAO.findByUuid(folderUuid);
        if(folderEntity == null) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        boolean isExists = fileDAO.existsByNameAndFolderUuid(fileName, folderUuid);
        if(isExists) {
            throw new CustomException(ALREADY_EXISTS_FILE);
        }

        String path = folderEntity.getParentPath() + folderEntity.getName() + "/";


        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        String filePath = uploadFileToS3(file, path);

        FileEntity fileEntity = new FileEntity(fileName, fileSize, fileExtension, filePath, folderEntity, userEntity);
        FileEntity savedFile = fileDAO.saveFile(fileEntity);

        userDAO.updateExtraVolume(userEntity, fileSize);

        HashMap<String, Object> body = new HashMap<>();
        body.put("saved_file", savedFile);

        ResponseDTO responseDTO = new ResponseDTO(200, "SUCCESS", LocalDateTime.now(), body);
        return responseDTO;
    }

    private String uploadFileToS3(MultipartFile file, String filePath) throws IOException {
        String fileName = file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, filePath + fileName, file.getInputStream(), objectMetadata);
        amazonS3Client.putObject(putObjectRequest);
        String path = amazonS3Client.getUrl(bucket, fileName).toString();
        return path;
    }
}
