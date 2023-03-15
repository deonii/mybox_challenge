package deonii.mybox.data.dao;

import deonii.mybox.data.entity.UserEntity;

public interface UserDAO {
    UserEntity saveUser(UserEntity user);

    Boolean checkEmail(String email);
}
