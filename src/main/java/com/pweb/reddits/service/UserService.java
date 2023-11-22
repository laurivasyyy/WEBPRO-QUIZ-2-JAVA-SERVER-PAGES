package com.pweb.reddits.service;

import com.pweb.reddits.entity.User;
import com.pweb.reddits.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    public Iterable<User> findAll() {
        return repo.findAll();
    }

    public void addUser(User user) {
        repo.save(user);
    }

    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }

    public User findByUsername(String username) {
        for (User u : repo.findAll()) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }

        return null;
    }

    public boolean authenticate(String username, String password) {
      User user = findByUsername(username);
      if (user == null) {
          return false;
      }
      return password == user.getPassword();
  }
}