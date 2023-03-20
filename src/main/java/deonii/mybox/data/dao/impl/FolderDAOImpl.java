package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.data.repository.FolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional
@AllArgsConstructor
public class FolderDAOImpl implements FolderDAO {

    private final FolderRepository folderRepository;

    @Override
    public FolderEntity createFolder(FolderEntity folderEntity) {
        FolderEntity saveFolderEntity = folderRepository.save(folderEntity);
        return saveFolderEntity;
    }

    @Override
    public FolderEntity findByUuid(UUID uuid) {
        FolderEntity folderEntity = folderRepository.findByUuid(uuid);
        return folderEntity;
    }

    @Override
    public boolean existsByNameAndParentPath(String name, String parentPath) {
        boolean isExists = folderRepository.existsByNameAndParentPath(name, parentPath);
        return isExists;
    }
}
