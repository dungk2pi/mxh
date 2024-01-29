package com.social.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.social.demo.models.Users;


public interface UsersRepository extends MongoRepository<Users, String> {
  Optional<Users> findByUsername(String username);

  Optional<Users> findByEmail(String email);

  Optional<Users> findByResetPwToken(int token);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Page<Users> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
