package deonii.mybox.data.dao;

import deonii.mybox.data.entity.UserEntity;

import java.util.UUID;

public interface UserDAO {
    UserEntity saveUser(UserEntity user);

    Boolean checkEmail(String email);

    UserEntity findByEmail(String email);

    void updateLastLogin(UserEntity user);

    UserEntity findByUuid(UUID uuid);
}
