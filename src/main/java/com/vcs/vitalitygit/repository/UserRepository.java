package com.vcs.vitalitygit.repository;

import com.vcs.vitalitygit.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByUsername(String username);
    List<User> findAllByUsernameContainingIgnoreCase(String pattern);
}
