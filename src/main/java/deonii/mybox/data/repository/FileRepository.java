package deonii.mybox.data.repository;

import deonii.mybox.data.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
}
