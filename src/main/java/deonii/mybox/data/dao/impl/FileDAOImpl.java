package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.FileDAO;
import deonii.mybox.data.entity.FileEntity;
import deonii.mybox.data.entity.SessionEntity;
import deonii.mybox.data.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class FileDAOImpl implements FileDAO {

    private final FileRepository fileRepository;

    @Override
    public FileEntity saveFile(FileEntity fileEntity) {
        FileEntity savedFileEntity = fileRepository.save(fileEntity);
        return savedFileEntity;
    }

    @Override
    public boolean existsByNameAndFolderUuid(String name, UUID folderUuid) {
        boolean isExists = fileRepository.existsByNameAndFolder_Uuid(name, folderUuid);
        return isExists;
    }

    @Override
    public FileEntity findByUuid(UUID fileUuid) {
        FileEntity fileEntity = fileRepository.findByUuid(fileUuid);
        return fileEntity;
    }

    @Override
    public void deleteFile(FileEntity fileEntity) {
        fileRepository.delete(fileEntity);
    }
}
