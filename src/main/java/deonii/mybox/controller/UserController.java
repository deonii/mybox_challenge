package deonii.mybox.controller;


import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.dto.UserResponseDTO;
import deonii.mybox.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/signup")
    public UserResponseDTO signup(@Valid @RequestBody UserRequestDTO userRequestDTO,
                       HttpServletResponse response) {
        UserResponseDTO userResponseDTO = userService.signup(userRequestDTO, response);
        return userResponseDTO;
    }

    @PostMapping("/login")
    public UserResponseDTO login(@Valid @RequestBody UserRequestDTO userRequestDTO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        UserResponseDTO userResponseDTO = userService.login(userRequestDTO, request, response);
        return userResponseDTO;
    }

    @PostMapping("/signout")
    public UserResponseDTO logout(HttpServletRequest request,
                                  HttpServletResponse response) {
        UserResponseDTO userResponseDTO = userService.logout(request, response);
        return userResponseDTO;
    }

    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        UUID userUuid = (UUID) request.getAttribute("userUuid");
        return "hello : " + userUuid.toString();
    }
}
