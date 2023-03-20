package deonii.mybox.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "session")
@Getter
public class SessionEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false, updatable = false)
    private UUID sessionId;

    @Column(name = "user_uuid", nullable = false, updatable = false)
    private UUID userUuid;

    @Column(name = "create_at")
    @CreatedDate
    private LocalDateTime createAt;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    public SessionEntity(UserEntity userEntity) {
        sessionId = UUID.randomUUID();
        userUuid = userEntity.getUuid();
        createAt = LocalDateTime.now();
        expireAt = LocalDateTime.now().plusDays(7);
    }
}
