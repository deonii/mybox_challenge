package deonii.mybox.data.repository;

import deonii.mybox.data.entity.FileEntity;
import deonii.mybox.data.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
}
