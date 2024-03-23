package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository implements QuestionInterface {
    public void saveQuestion(QuestionInterface questionInterface) {
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public int postQuestion(Question question, MultipartFile image) {
        final String insertSql = "INSERT INTO question (author, title, text, creationTime, picturePath) VALUES (?,?,?,NOW(),?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
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
            Path filePath = Paths.get(uploadDir, "Q" + newQuestionID + "U" + userID + image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4));
            Files.write(filePath, image.getBytes());

            final String updatePicturePathSql = "UPDATE question SET picturePath = ? WHERE id = ?";
            jdbcTemplate.update(updatePicturePathSql, filePath.toString(), newQuestionID);


        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public List<Question> getQuestionsOfUser(String username) {


        final String selectSql = "SELECT q.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tagNames " +
                "FROM question q " +
                "LEFT JOIN question_tag_join qt ON q.id = qt.question_id " +
                "LEFT JOIN tag t ON qt.tag_id = t.id " +
                "WHERE q.author = ? " +
                "GROUP BY q.id " +
                "ORDER BY q.creationTime DESC";

        List<Question> questions = jdbcTemplate.query(selectSql, new Object[]{username}, (rs, rowNum) -> {
            return new Question(
                    rs.getLong("id"),
                    rs.getString("author"),
                    rs.getString("title"),
                    rs.getString("text"),
                    rs.getTimestamp("creationTime").toLocalDateTime(),
                    rs.getString("picturePath"),
                    rs.getString("tagNames"));
        });
        return questions;
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

    public int deleteQuestion(String username, Long questionID) {
        //check if the user is the author of the question
        final String selectSql = "SELECT author FROM question WHERE id = ?";
        String author = jdbcTemplate.queryForObject(selectSql, String.class, questionID);

        if (author.equals(username)) {
            String deleteQuestionTagJoinSql = "DELETE FROM question_tag_join WHERE question_id = ?";
            jdbcTemplate.update(deleteQuestionTagJoinSql, questionID);

            final String selectPicturePathSql = "SELECT picturePath FROM question WHERE id = ?";
            String picturePath = jdbcTemplate.queryForObject(selectPicturePathSql, String.class, questionID);
            Path filePath = Paths.get(picturePath);
            try {
                Files.delete(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }


            String deleteSql = "DELETE FROM question WHERE id = ?";
            jdbcTemplate.update(deleteSql, questionID);
            return 1;
        } else {
            return 0;
        }
    }

    public int updateQuestion(String author, int id, String title, String text, String tags, MultipartFile image) throws IOException {

        try {;

            final String selectSql = "SELECT q.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tagNames " +
                    "FROM question q " +
                    "LEFT JOIN question_tag_join qt ON q.id = qt.question_id " +
                    "LEFT JOIN tag t ON qt.tag_id = t.id " +
                    "WHERE q.id = ? ";

            Question question = jdbcTemplate.queryForObject(selectSql, new Object[]{id}, (rs, rowNum) -> {
                return new Question(
                        rs.getLong("id"),
                        rs.getString("author"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getTimestamp("creationTime").toLocalDateTime(),
                        rs.getString("picturePath"),
                        rs.getString("tagNames"));
                    });

                if (!question.getAuthor().equals(author)) {
                    return 0;
                }

                //get user id
                final String selectUserSql = "SELECT id FROM user WHERE username = ?";
                int userID = jdbcTemplate.queryForObject(selectUserSql, Integer.class, author);

                String uploadDir = "./images";

                Path dirPath = Paths.get(uploadDir);

                // Save the file to the specified directory
                Path filePath = Paths.get(uploadDir, "Q" + question.getId() + "U" + userID + image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4));
                Files.write(filePath, image.getBytes());

                final String updatePicturePathSql = "UPDATE question SET picturePath = ? WHERE id = ?";
                jdbcTemplate.update(updatePicturePathSql, filePath.toString(), question.getId());



                if(title == null) {
                    title = question.getTitle();
                }
                if(text == null) {
                    text = question.getText();
                }
                if(tags == null) {
                    tags = question.getTags();
                }

                //delete old tags
                final String deleteQuestionTagJoinSql = "DELETE FROM question_tag_join WHERE question_id = ?";
                jdbcTemplate.update(deleteQuestionTagJoinSql, id);



                String[] tagsArray = tags.split(",");


                //insert new tags if they don't exist

            for (String tag : tagsArray) {
                tag = tag.toLowerCase();
                final String insertTagSql = "INSERT INTO tag (name) VALUES (?) ON DUPLICATE KEY UPDATE name=name";
                //create prepared statement and execute
                jdbcTemplate.update(insertTagSql, tag);
            }

            for (String tag : tagsArray) {
                //for each tag find the tag id
                tag = tag.toLowerCase();
                final String selectTagSql = "SELECT id FROM tag WHERE name = ?";
                int tagID = jdbcTemplate.queryForObject(selectTagSql, Integer.class, tag);
                final String insertQuestionTagSql = "INSERT INTO question_tag_join (question_id, tag_id) VALUES (?,?)";
                jdbcTemplate.update(insertQuestionTagSql, id, tagID);
            }


            final String updateSql = "UPDATE question SET title = ?, text = ? WHERE id = ?";

            jdbcTemplate.update(updateSql, title, text, question.getId());

            return 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
