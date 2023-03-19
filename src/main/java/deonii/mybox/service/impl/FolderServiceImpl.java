package deonii.mybox.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class FolderServiceImpl implements FolderService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Autowired
    private FolderDAO folderDAO;

    @Override
    public FolderEntity createFolder(FolderEntity folderEntity) {
        String folderName = folderEntity.getName();
        createFolderInS3(folderName);
        FolderEntity savedFolderEntity = folderDAO.createFolder(folderEntity);
        return savedFolderEntity;
    }

    private void createFolderInS3(String folderName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,
                folderName + "/", emptyContent, metadata);
        amazonS3Client.putObject(putObjectRequest);
    }
}
