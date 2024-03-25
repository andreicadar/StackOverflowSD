package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository implements UserInterface {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int deleteUserByUsername(String username) {
        return jdbcTemplate.update("DELETE FROM user WHERE username = ?", username);
    }

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public <S extends User> S save(S entity) {
        return null;
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<User> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }

    @Override
    public Iterable<User> findAllById(Iterable<Integer> integers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }

    public int updateUser(String username, String newUsername, String password, String email) {
        String sqlSelectQuery = "SELECT * FROM user WHERE username = ?";
        User user = jdbcTemplate.queryForObject(sqlSelectQuery, new Object[]{username}, (resultSet, i) -> {
            User user1 = new User();
            user1.setId(resultSet.getLong("id"));
            user1.setUsername(resultSet.getString("username"));
            user1.setEmail(resultSet.getString("email"));
            user1.setPassword(resultSet.getString("password"));
            user1.setRole(resultSet.getString("role"));
            user1.setScore(resultSet.getFloat("score"));
            return user1;
        });

        if(newUsername == null) {
            newUsername = user.getUsername();
        }
        if(password == null) {
            password = user.getPassword();
        }
        else
        {
            password = encoder.encode(password);
        }
        if(email == null) {
            email = user.getEmail();
        }

        String sqlUpdateQuery = "UPDATE user SET username = ?, password = ?, email = ? WHERE username = ?";
        return jdbcTemplate.update(sqlUpdateQuery, newUsername, password, email, username);

    }
}
