package deonii.mybox.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import deonii.mybox.config.S3Config;
import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.FileResponseDTO;
import deonii.mybox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;


@Service
public class FileServiceImpl implements FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Override
    public FileResponseDTO uploadFile(FileRequestDTO fileRequestDTO) throws IOException {
        MultipartFile file = fileRequestDTO.getFile();
        String fileName = file.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file.getInputStream(), objectMetadata);
        amazonS3Client.putObject(putObjectRequest);
        String path = amazonS3Client.getUrl(bucket, fileName).toString();
        FileResponseDTO fileResponseDTO = new FileResponseDTO("Upload Done", path, LocalDateTime.now());

        return fileResponseDTO;
    }
}
