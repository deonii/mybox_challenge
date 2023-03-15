package deonii.mybox.service;

import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.dto.UserResponseDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public interface UserService {
    public UserResponseDTO signup(UserRequestDTO userRequestDTO, HttpServletResponse response);

    public UserResponseDTO login(UserRequestDTO userRequestDTO, HttpServletRequest request, HttpServletResponse response);

    public UserResponseDTO logout(HttpServletRequest request, HttpServletResponse response);
}
