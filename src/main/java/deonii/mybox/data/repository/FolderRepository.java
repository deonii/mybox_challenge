package deonii.mybox.data.repository;

import deonii.mybox.data.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FolderRepository extends JpaRepository<FolderEntity, UUID> {
    FolderEntity findByUuid(UUID uuid);
    boolean existsByNameAndParentPath(String name, String parentPath);
}
