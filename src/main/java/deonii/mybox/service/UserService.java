package deonii.mybox.service;

import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.dto.UserResponseDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public interface UserService {
    UserResponseDTO signup(UserRequestDTO userRequestDTO, HttpServletResponse response);

    UserResponseDTO login(UserRequestDTO userRequestDTO, HttpServletRequest request, HttpServletResponse response);

    UserResponseDTO logout(HttpServletRequest request, HttpServletResponse response);
}
