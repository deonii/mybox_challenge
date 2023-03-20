package deonii.mybox.service;

import deonii.mybox.data.dto.ResponseDTO;
import deonii.mybox.data.dto.UserRequestDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    ResponseDTO signup(UserRequestDTO userRequestDTO, HttpServletResponse response);

    ResponseDTO login(UserRequestDTO userRequestDTO, HttpServletRequest request, HttpServletResponse response);

    ResponseDTO logout(HttpServletRequest request, HttpServletResponse response);
}
