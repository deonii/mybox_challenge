package deonii.mybox.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import deonii.mybox.config.S3Config;
import deonii.mybox.data.dto.FileRequestDTO;
import deonii.mybox.data.dto.FileResponseDTO;
import deonii.mybox.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class FileController {

//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    @Autowired
//    private AmazonS3Client amazonS3Client;
//
//    @Autowired
//    private S3Config s3Config;

    @Autowired
    private FileService fileService;

    @PostMapping("/file")
    private FileResponseDTO uploadToS3(FileRequestDTO fileRequestDTO) throws IOException {
        FileResponseDTO fileResponseDTO = fileService.uploadFile(fileRequestDTO);
        return fileResponseDTO;



    }

}
