package deonii.mybox.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String message;
    private LocalDateTime timestamp;
    private LocalDateTime expireAt;
}
