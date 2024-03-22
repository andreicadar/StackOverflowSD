package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class QuestionRepository implements QuestionInterface{
    public void saveQuestion(QuestionInterface questionInterface) {
    }
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public int postQuestion(Question question, MultipartFile image) {
        final String insertSql = "INSERT INTO question (author, title, text, creationTime, picturePath) VALUES (?,?,?,NOW(),?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
    try {

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[] {"id"});
            ps.setString(1, question.getAuthor());
            ps.setString(2, question.getTitle());
            ps.setString(3, question.getText());
            ps.setString(4, question.getPicturePath());
            return ps;
        }, keyHolder);

        int newQuestionID = keyHolder.getKey().intValue();

        String[] tags = question.getTags().split(",");


        for (String tag : tags) {
            tag = tag.toLowerCase();
            final String insertTagSql = "INSERT INTO tag (name) VALUES (?) ON DUPLICATE KEY UPDATE name=name";
            //create prepared statement and execute
            jdbcTemplate.update(insertTagSql, tag);
        }

        for (String tag : tags) {
            //for each tag find the tag id
            tag = tag.toLowerCase();
            final String selectTagSql = "SELECT id FROM tag WHERE name = ?";
            int tagID = jdbcTemplate.queryForObject(selectTagSql, Integer.class, tag);
            final String insertQuestionTagSql = "INSERT INTO question_tag_join (question_id, tag_id) VALUES (?,?)";
            jdbcTemplate.update(insertQuestionTagSql, newQuestionID, tagID);
        }

        //get user id
        final String selectUserSql = "SELECT id FROM user WHERE username = ?";
        int userID = jdbcTemplate.queryForObject(selectUserSql, Integer.class, question.getAuthor());


        String uploadDir = "./images";

        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Save the file to the specified directory
        Path filePath = Paths.get(uploadDir, "Q" + newQuestionID + "U" + userID + image.getOriginalFilename().substring(image.getOriginalFilename().length()-4));
        Files.write(filePath, image.getBytes());

        final String updatePicturePathSql = "UPDATE question SET picturePath = ? WHERE id = ?";
        jdbcTemplate.update(updatePicturePathSql, filePath.toString(), newQuestionID);


    }
    catch (Exception e) {
        e.printStackTrace();
        return 0;
    }
    return 1;
    }


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
}
