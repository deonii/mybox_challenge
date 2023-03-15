package deonii.mybox.data.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "file")
public class FileEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false)
    private String path;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_uuid")
    private FolderEntity folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    private UserEntity owner;
}
