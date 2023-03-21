package deonii.mybox.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import deonii.mybox.data.dao.FileDAO;
import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.entity.FileEntity;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.error.CustomException;
import deonii.mybox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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

        if(!folderEntity.getOwner().equals(userEntity)) {
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

    @Override
    public ResponseDTO deleteFile(UUID folderUuid, UUID fileUuid, UUID userUuid) {
        UserEntity userEntity = userDAO.findByUuid(userUuid);
        if(userEntity == null) {
            throw new CustomException(NOT_EXISTS_UUID);
        }

        FolderEntity folderEntity = folderDAO.findByUuid(folderUuid);
        if(folderEntity == null) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        FileEntity fileEntity = fileDAO.findByUuid(fileUuid);
        if(fileEntity == null) {
            throw new CustomException(NOT_EXISTS_FILE);
        }

        if(!fileEntity.getFolder().equals(folderEntity)) {
            throw new CustomException(NOT_EXISTS_FILE);
        }

        if(!fileEntity.getOwner().equals(userEntity)) {
            throw new CustomException(NOT_EXISTS_FILE);
        }

        String filePath = folderEntity.getParentPath() + folderEntity.getName() + "/" + fileEntity.getName();

        deleteFileInS3(filePath);
        fileDAO.deleteFile(fileEntity);

        ResponseDTO responseDTO = new ResponseDTO(200, "DELETE", LocalDateTime.now(), null);
        return responseDTO;
    }

    @Override
    public StreamingResponseBody downloadFile(UUID folderUuid, UUID fileUuid, UUID userUuid, HttpServletResponse response) {
        UserEntity userEntity = userDAO.findByUuid(userUuid);
        if(userEntity == null) {
            throw new CustomException(NOT_EXISTS_UUID);
        }

        FolderEntity folderEntity = folderDAO.findByUuid(folderUuid);
        if(folderEntity == null) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        FileEntity fileEntity = fileDAO.findByUuid(fileUuid);
        if(fileEntity == null) {
            throw new CustomException(NOT_EXISTS_FILE);
        }

        if(!fileEntity.getFolder().equals(folderEntity)) {
            throw new CustomException(NOT_EXISTS_FILE);
        }

        if(!fileEntity.getOwner().equals(userEntity)) {
            throw new CustomException(NOT_EXISTS_FILE);
        }

        return downloadFileFromS3(fileEntity, folderEntity, response);
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

    private void deleteFileInS3(String filePath) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, filePath);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    private StreamingResponseBody downloadFileFromS3(FileEntity fileEntity,
                                                     FolderEntity folderEntity,
                                                     HttpServletResponse response){
        String fileName = fileEntity.getName();
        String filePath = folderEntity.getParentPath() + folderEntity.getName() + "/";

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, filePath + fileName);
        S3Object s3Object = amazonS3Client.getObject(getObjectRequest);

        InputStream inputStream = s3Object.getObjectContent();
        response.setContentType(s3Object.getObjectMetadata().getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Length", String.valueOf(s3Object.getObjectMetadata().getContentLength()));

        return outputStream -> {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
            }
            inputStream.close();
        };
    }
}
