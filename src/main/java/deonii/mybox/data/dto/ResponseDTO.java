package deonii.mybox.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

    private int status;

    private String message;

    private LocalDateTime timestamp;

    private HashMap<String, Object> body;
}
