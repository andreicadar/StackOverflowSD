package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends
        CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
