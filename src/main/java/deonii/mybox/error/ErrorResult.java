package deonii.mybox.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResult {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResult(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}
