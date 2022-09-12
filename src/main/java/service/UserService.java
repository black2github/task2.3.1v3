package service;

import model.User;

import java.util.List;

public interface UserService {
    User create(User user);
    List<User> list(int count);
    List<User> listAll();
    User find(Long id);
    void delete(User user);
    User update(long id, User user);
}
