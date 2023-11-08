package ru.yandex.practicum.filmorate.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserDao;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

//    private final UserDao userDao;
//
//    @Test
//    @BeforeEach
//    public void cleanUsers() {
//        List<User> users = userDao.findAllUsers();
//        users.forEach(user -> userDao.deleteUserById(user.getId()));
//    }
//
//    @Test
//    public void testOneUserCreate() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//
//        Assertions.assertEquals("Test", createdUser.getName());
//        Assertions.assertEquals("mail@etcdev.ru", createdUser.getEmail());
//        Assertions.assertEquals("canoutis", createdUser.getLogin());
//        Assertions.assertEquals(LocalDate.of(1998, 5, 27), createdUser.getBirthday());
//    }
//
//    @Test
//    public void testOneUserGetById() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//
//        User foundUser = userDao.getUserById(createdUser.getId());
//
//        Assertions.assertEquals(createdUser.getId(), foundUser.getId());
//        Assertions.assertEquals("Test", foundUser.getName());
//        Assertions.assertEquals("mail@etcdev.ru", foundUser.getEmail());
//        Assertions.assertEquals("canoutis", foundUser.getLogin());
//        Assertions.assertEquals(LocalDate.of(1998, 5, 27), foundUser.getBirthday());
//    }
//
//    @Test
//    public void testOneUserUpdate() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//        createdUser.setName("Updated Name");
//        createdUser.setEmail("mail@iotachi.ru");
//        createdUser.setLogin("iota");
//        createdUser.setBirthday(LocalDate.of(1970, 1, 1));
//        User foundUser = userDao.updateUser(createdUser);
//        Assertions.assertEquals(createdUser.getId(), foundUser.getId());
//        Assertions.assertEquals("Updated Name", foundUser.getName());
//        Assertions.assertEquals("mail@iotachi.ru", foundUser.getEmail());
//        Assertions.assertEquals("iota", foundUser.getLogin());
//        Assertions.assertEquals(LocalDate.of(1970, 1, 1), foundUser.getBirthday());
//    }
//
//    @Test
//    public void testGetEmptyUserFriends() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//        Assertions.assertEquals(0, userDao.getAllUserFriends(createdUser.getId()).size());
//
//    }
//
//    @Test
//    public void testGetOneFriendUserByOneFriend() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//        User tempUser2 = User.builder()
//                .name("Test2")
//                .email("mail2@etcdev.ru")
//                .login("canoutis2")
//                .birthday(LocalDate.of(1990, 5, 27))
//                .build();
//        User createdUser2 = userDao.createUser(tempUser2);
//        userDao.addFriend(createdUser.getId(), createdUser2.getId());
//        List<User> friends = userDao.getAllUserFriends(createdUser.getId());
//        Assertions.assertEquals(1, friends.size());
//        Assertions.assertEquals(createdUser2, friends.get(0));
//    }
//
//    @Test
//    public void testRemoveOneFriendUserByOneFriend() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//        User tempUser2 = User.builder()
//                .name("Test2")
//                .email("mail2@etcdev.ru")
//                .login("canoutis2")
//                .birthday(LocalDate.of(1990, 5, 27))
//                .build();
//        User createdUser2 = userDao.createUser(tempUser2);
//        userDao.addFriend(createdUser.getId(), createdUser2.getId());
//        Assertions.assertEquals(1, userDao.getAllUserFriends(createdUser.getId()).size());
//        userDao.unfriend(createdUser.getId(), createdUser2.getId());
//        Assertions.assertEquals(0, userDao.getAllUserFriends(createdUser.getId()).size());
//    }
//
//    @Test
//    public void testGetOneMutualFriendUser() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//        User tempUser2 = User.builder()
//                .name("Test2")
//                .email("mail2@etcdev.ru")
//                .login("canoutis2")
//                .birthday(LocalDate.of(1990, 5, 27))
//                .build();
//        User createdUser2 = userDao.createUser(tempUser2);
//        User tempUser3 = User.builder()
//                .name("Test3")
//                .email("mail3@etcdev.ru")
//                .login("canoutis3")
//                .birthday(LocalDate.of(1980, 5, 27))
//                .build();
//        User createdUser3 = userDao.createUser(tempUser3);
//        userDao.addFriend(createdUser.getId(), createdUser3.getId());
//        userDao.addFriend(createdUser2.getId(), createdUser3.getId());
//        Assertions.assertEquals(1, userDao.getCommonFriends(createdUser.getId(), createdUser2.getId()).size());
//        Assertions.assertEquals(createdUser3, userDao.getCommonFriends(createdUser.getId(), createdUser2.getId()).get(0));
//    }
//
//    @Test
//    public void testRemoveOneUserGetById() {
//        User tempUser = User.builder()
//                .name("Test")
//                .email("mail@etcdev.ru")
//                .login("canoutis")
//                .birthday(LocalDate.of(1998, 5, 27))
//                .build();
//        User createdUser = userDao.createUser(tempUser);
//        userDao.deleteUserById(createdUser.getId());
//        Assertions.assertTrue(userDao.findAllUsers().isEmpty());
//
//    }
}
