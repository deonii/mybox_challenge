package deonii.mybox.data.dao.impl;

import deonii.mybox.data.dao.UserDAO;
import deonii.mybox.data.dto.UserRequestDTO;
import deonii.mybox.data.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


@SpringBootTest
@Transactional
class UserDAOImplTest {

    @Autowired
    UserDAO userDAO;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        UserRequestDTO userRequestDTO = new UserRequestDTO("deonii@test.com", "password");
        UserEntity userEntity = new UserEntity(userRequestDTO);
        userEntity.encryptPassword(passwordEncoder);
        userDAO.saveUser(userEntity);
    }

    @Test
    void findByEmail() {
        // when
        UserEntity findUser = userDAO.findByEmail("deonii@test.com");

        // then
        Assertions.assertThat(findUser).isNotNull();
    }

    @Test
    void saveUser() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO("create_deonii@test.com", "password");
        UserEntity userEntity = new UserEntity(userRequestDTO);
        userEntity.encryptPassword(passwordEncoder);
        UserEntity savedUser = userDAO.saveUser(userEntity);

        // when
        UserEntity findUser = userDAO.findByEmail("create_deonii@test.com");

        // then
        Assertions.assertThat(savedUser).isEqualTo(findUser);
    }

    @Test
    void encryptPassword() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO("create_deonii@test.com", "password");
        UserEntity userEntity = new UserEntity(userRequestDTO);

        // when
        userEntity.encryptPassword(passwordEncoder);

        // then
        Assertions.assertThat(userEntity.getPassword()).isNotEqualTo(userRequestDTO.getPassword());

        boolean isMatch = userEntity.matchPassword(passwordEncoder, userRequestDTO.getPassword());

        Assertions.assertThat(isMatch).isTrue();
    }

    @Test
    void checkEmail() {
        // when
        Boolean isExists1 = userDAO.checkEmail("deonii@test.com");
        Boolean isExists2 = userDAO.checkEmail("deonii_test@test.com");

        // then
        Assertions.assertThat(isExists1).isTrue();
        Assertions.assertThat(isExists2).isFalse();
    }

    @Test
    void updateLastLogin() {
        // given
        UserEntity findUser = userDAO.findByEmail("deonii@test.com");
        LocalDateTime lastLogin = findUser.getLastLogin();

        // when
        userDAO.updateLastLogin(findUser);

        // then
        Assertions.assertThat(lastLogin).isNotEqualTo(findUser.getLastLogin());
    }

    @Test
    void findByUuid() {
        // given
        UserRequestDTO userRequestDTO = new UserRequestDTO("create_deonii@test.com", "password");
        UserEntity userEntity = new UserEntity(userRequestDTO);
        userEntity.encryptPassword(passwordEncoder);
        UserEntity savedUser = userDAO.saveUser(userEntity);
        UUID userUuid = savedUser.getUuid();

        // when
        UserEntity findUser = userDAO.findByUuid(userUuid);

        // then
        Assertions.assertThat(savedUser).isEqualTo(findUser);
    }

    @Test
    void updateExtraVolume() {
        // given
        Long fileSize = 12345L;
        UserEntity findUser = userDAO.findByEmail("deonii@test.com");
        Long extraVolume = findUser.getExtraVolume();

        // when
        userDAO.updateExtraVolume(findUser, fileSize);

        // then
        Assertions.assertThat(extraVolume).isEqualTo(findUser.getExtraVolume() + fileSize);
    }
}