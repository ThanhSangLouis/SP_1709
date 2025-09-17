package vn.hcmute.service;

import vn.hcmute.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    
}
