package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.Answer;
import org.example.stackoverflowsd.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AnswerRepository implements  AnswerInterface{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public <S extends Question> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Question> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Question> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public Iterable<Question> findAll() {
        return null;
    }

    @Override
    public Iterable<Question> findAllById(Iterable<Integer> integers) {
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
    public void delete(Question entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends Question> entities) {

    }

    @Override
    public void deleteAll() {

    }


    public int answerQuestion(Answer answer, MultipartFile image, int questionID) throws IOException {
        //check if question exists
        String sql = "SELECT * FROM question WHERE id = ?";
        Question question = jdbcTemplate.queryForObject(sql, new Object[]{questionID}, (rs, rowNum) -> {
            Question q = new Question();
            q.setId((long) rs.getInt("id"));
            return q;
        });

        if(question == null) {
            return 0;
        }

        //get user if from author
        sql = "SELECT id FROM user WHERE username = ?";
        int authorID = jdbcTemplate.queryForObject(sql, new Object[]{answer.getAuthor()}, Integer.class);

        //insert answer
        String insertSql = "INSERT INTO answer (userID, text, creationTime, questionID) VALUES (?, ?, NOW(), ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
                ps.setInt(1, authorID);
                ps.setString(2, answer.getText());
                ps.setInt(3, questionID);
                return ps;
            }, keyHolder);

            int newAnswerID = Objects.requireNonNull(keyHolder.getKey()).intValue();

            String uploadDir = "./images";

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = Paths.get(uploadDir, "A" + newAnswerID + "U" + authorID + image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4));
            Files.write(filePath, image.getBytes());

            final String updatePicturePathSql = "UPDATE answer SET picturePath = ? WHERE id = ?";
            jdbcTemplate.update(updatePicturePathSql, filePath.toString(), newAnswerID);

            return 1;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
