package deonii.mybox.data.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotEmpty(message = "email은 필수 항목 입니다.")
    private String email;

    @NotNull
    private String password;
}
