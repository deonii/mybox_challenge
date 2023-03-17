package deonii.mybox.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {
    private String message;
    @JsonProperty("file_path")
    private String filePath;
    private LocalDateTime timestamp;
}
