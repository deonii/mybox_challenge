package deonii.mybox.controller;


import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.dto.UserResponseDTO;
import deonii.mybox.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/signup")
    public UserResponseDTO signup(@Valid @RequestBody UserRequestDTO userRequestDTO,
                       @Value("${session.cookie.name}") String cookieName,
                       HttpServletResponse response
                       ) {
        UserResponseDTO userResponseDTO = userService.signup(userRequestDTO, response, cookieName);
        return userResponseDTO;
    }
}
