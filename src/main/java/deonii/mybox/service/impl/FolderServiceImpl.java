package deonii.mybox.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import deonii.mybox.data.dao.FileDAO;
import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.FolderRequestDTO;
import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.entity.FileEntity;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.error.CustomException;
import deonii.mybox.service.FolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static deonii.mybox.error.ErrorCode.*;

@Service
@Slf4j
public class FolderServiceImpl implements FolderService {

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
    public void createRootFolder(FolderEntity folderEntity) {
        String folderName = folderEntity.getName();
        String parentPath = "";
        createFolderInS3(folderName, parentPath);
        folderDAO.createFolder(folderEntity);
    }

    @Override
    public ResponseDTO createFolder(FolderRequestDTO folderRequestDTO, UUID parentUuid, UUID userUuid) {
        UserEntity userEntity = userDAO.findByUuid(userUuid);
        if (userEntity == null) {
            throw new CustomException(NOT_EXISTS_UUID);
        }

        FolderEntity parentFolderEntity = folderDAO.findByUuid(parentUuid);
        if (parentFolderEntity == null) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        if (!parentFolderEntity.getOwner().equals(userEntity)) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        String folderName = folderRequestDTO.getName();
        String parentPath = parentFolderEntity.getParentPath() + parentFolderEntity.getName() + "/";

        boolean isExists = folderDAO.existsByNameAndParentPath(folderName, parentPath);
        if (isExists) {
            throw new CustomException(ALREADY_EXISTS_FOLDER);
        }
        createFolderInS3(folderName, parentPath);

        FolderEntity saveFolderEntity = new FolderEntity(folderName, parentFolderEntity, userEntity);
        folderDAO.createFolder(saveFolderEntity);

        HashMap<String, Object> body = new HashMap<>();
        body.put("folder_path", parentPath + folderName);

        ResponseDTO responseDTO = new ResponseDTO(201, "SUCCESS", LocalDateTime.now(), body);

        return responseDTO;
    }

    @Override
    public ResponseDTO browseFolder(UUID folderUuid, UUID userUuid) {
        UserEntity userEntity = userDAO.findByUuid(userUuid);
        if (userEntity == null) {
            throw new CustomException(NOT_EXISTS_UUID);
        }

        FolderEntity folderEntity = folderDAO.findByUuid(folderUuid);
        if (folderEntity == null) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        if (!folderEntity.getOwner().equals(userEntity)) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        List<FolderEntity> folderEntityList = folderDAO.findByParentUuid(folderUuid);
        List<FileEntity> fileEntityList = fileDAO.findFileByFolderUuid(folderUuid);

        HashMap<String, Object> body = new HashMap<>();
        body.put("folder_list", folderEntityList);
        body.put("file_list", fileEntityList);
        if (folderEntity.getParent() != null) {
            body.put("parent_uuid", folderEntity.getParent().getUuid());
        }

        ResponseDTO responseDTO = new ResponseDTO(200, "SUCCESS", LocalDateTime.now(), body);
        return responseDTO;
    }

    @Override
    public ResponseDTO deleteFolder(UUID folderUuid, UUID userUuid) {
        UserEntity userEntity = userDAO.findByUuid(userUuid);
        if (userEntity == null) {
            throw new CustomException(NOT_EXISTS_UUID);
        }

        FolderEntity folderEntity = folderDAO.findByUuid(folderUuid);
        if (folderEntity == null) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        if (!folderEntity.getOwner().equals(userEntity)) {
            throw new CustomException(NOT_EXISTS_FOLDER);
        }

        if(folderEntity.getParent() == null) {
            throw new CustomException(CAN_NOT_ROOT_FOLDER);
        }

        String folderName = folderEntity.getName();
        FolderEntity parentFolderEntity = folderEntity.getParent();
        String parentPath = parentFolderEntity.getParentPath() + parentFolderEntity.getName() + "/";

        deleteFolderInS3(folderName, parentPath);

        folderDAO.deleteFolder(folderEntity);
        ResponseDTO responseDTO = new ResponseDTO(200, "DELETE", LocalDateTime.now(), null);
        return responseDTO;
    }


    private void createFolderInS3(String folderName, String parentPath) {
        String folderPath = parentPath + folderName + "/";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, folderPath, emptyContent, metadata);
        amazonS3Client.putObject(putObjectRequest);
    }

    public void deleteFolderInS3(String folderName, String parentPath) {
        String folderPath = parentPath + folderName + "/";
        List<String> keysToDelete = new ArrayList<>();
        for (S3ObjectSummary summary : amazonS3Client.listObjects(bucket, folderPath).getObjectSummaries()) {
            keysToDelete.add(summary.getKey());
        }

        // 삭제 요청 객체를 생성합니다.
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keysToDelete.toArray(new String[0]));

        // 폴더 내의 모든 객체를 삭제합니다.
        DeleteObjectsResult result = amazonS3Client.deleteObjects(deleteObjectsRequest);
        log.info("Deleted objects: {}", result.getDeletedObjects().toString());
    }
}
