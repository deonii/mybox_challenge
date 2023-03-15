package deonii.mybox.data.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "folder")
public class FolderEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid")
    private FolderEntity parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    private List<FolderEntity> children;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    private UserEntity owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "folder", cascade = CascadeType.ALL)
    private List<FileEntity> files;
}
