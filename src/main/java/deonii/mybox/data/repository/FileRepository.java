package deonii.mybox.data.repository;

import deonii.mybox.data.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    boolean existsByNameAndFolder_Uuid(String name, UUID folderUuid);

    FileEntity findByUuid(UUID fileUuid);

    List<FileEntity> findByFolder_Uuid(UUID folderUuid);
}
