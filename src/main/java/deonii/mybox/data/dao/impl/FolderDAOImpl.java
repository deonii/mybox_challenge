package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.FolderDAO;
import deonii.mybox.data.entity.FolderEntity;
import deonii.mybox.data.repository.FolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public FolderEntity findByNameAndParentPath(String name, String parentPath) {
        FolderEntity folderEntity = folderRepository.findByNameAndParentPath(name, parentPath);
        return folderEntity;
    }

    @Override
    public boolean existsByNameAndParentPath(String name, String parentPath) {
        boolean isExists = folderRepository.existsByNameAndParentPath(name, parentPath);
        return isExists;
    }

    @Override
    public List<FolderEntity> findByParentUuid(UUID folderUuid) {
        List<FolderEntity> folderEntityList = folderRepository.findByParent_Uuid(folderUuid);
        return folderEntityList;
    }

    @Override
    public void deleteFolder(FolderEntity folderEntity) {
        folderRepository.delete(folderEntity);
    }
}
