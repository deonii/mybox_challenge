package deonii.mybox.controller;


import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.functions.UserFunctions;
import deonii.mybox.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserFunctions userFunctions;

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

    @GetMapping("/user-info")
    public ResponseDTO getUserInfo(HttpServletRequest request) {
        UUID userUuid = userFunctions.getUserUuidFromRequest(request);
        ResponseDTO responseDTO = userService.getUserInfo(userUuid);
        return responseDTO;
    }
}
