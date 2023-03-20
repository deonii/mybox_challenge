package deonii.mybox.controller;


import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseDTO signup(@Valid @RequestBody UserRequestDTO userRequestDTO,
                              HttpServletResponse response) {
        ResponseDTO responseDTO = userService.signup(userRequestDTO, response);
        return responseDTO;
    }

    @PostMapping("/login")
    public ResponseDTO login(@Valid @RequestBody UserRequestDTO userRequestDTO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        ResponseDTO responseDTO = userService.login(userRequestDTO, request, response);
        return responseDTO;
    }

    @PostMapping("/signout")
    public ResponseDTO logout(HttpServletRequest request,
                                  HttpServletResponse response) {
        ResponseDTO responseDTO = userService.logout(request, response);
        return responseDTO;
    }
}
