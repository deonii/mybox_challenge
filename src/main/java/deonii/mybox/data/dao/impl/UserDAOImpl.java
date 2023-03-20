package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.entity.UserEntity;
import deonii.mybox.data.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final UserRepository userRepository;

    @Override
    public UserEntity saveUser(UserEntity user) {
        UserEntity saveUser = userRepository.save(user);
        return saveUser;
    }

    @Override
    public Boolean checkEmail(String email) {
        boolean existUser = userRepository.existsByEmail(email);
        return existUser;
    }

    @Override
    public UserEntity findByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        return userEntity;
    }

    @Override
    public void updateLastLogin(UserEntity user) {
        user.setLastLogin(LocalDateTime.now());
    }

    @Override
    public UserEntity findByUuid(UUID uuid) {
        UserEntity userEntity = userRepository.findByUuid(uuid);
        return userEntity;
    }

    @Override
    public void updateExtraVolume(UserEntity userEntity, Long fileSize) {
        userEntity.setExtraVolume(userEntity.getExtraVolume() - fileSize);
    }
}
