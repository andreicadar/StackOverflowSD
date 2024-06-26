package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.Answer;
import org.example.stackoverflowsd.model.Question;
import org.example.stackoverflowsd.model.User;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AnswerRepository implements  AnswerInterface{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired UserRepository userRepository;

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


    public int answerQuestion(Answer answer, MultipartFile image, Long questionID) throws IOException {
        String sql = "SELECT * FROM question WHERE id = ?";
        Question question = jdbcTemplate.queryForObject(sql, new Object[]{questionID}, (rs, rowNum) -> {
            Question q = new Question();
            q.setId((long) rs.getInt("id"));
            return q;
        });

        if(question == null) {
            return 0;
        }

        sql = "SELECT id FROM user WHERE username = ?";
        int authorID = jdbcTemplate.queryForObject(sql, new Object[]{answer.getAuthor()}, Integer.class);

        String insertSql = "INSERT INTO answer (userID, text, creationTime, questionID) VALUES (?, ?, NOW(), ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
                ps.setInt(1, authorID);
                ps.setString(2, answer.getText());
                ps.setLong(3, questionID);
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

    public int deleteAnswer(String username, Long answerID) {
        try {
            String sql = "SELECT * FROM answer WHERE id = ?";
            Answer answer = jdbcTemplate.queryForObject(sql, new Object[]{answerID}, (rs, rowNum) -> {
                Answer a = new Answer();
                a.setId((long) rs.getInt("id"));
                a.setUserID(rs.getInt("userID"));
                return a;
            });
            if (answer == null) {
                return 2;
            }

            sql = "SELECT id FROM user WHERE username = ?";
            int authorID = jdbcTemplate.queryForObject(sql, new Object[]{username}, Integer.class);
            if (authorID != answer.getUserID() && userRepository.verifyIfUserHasARole(username, "ROLE_MODERATOR") == 0) {
                return 0;

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }

        try {
            String deleteVotesSql = "DELETE FROM user_answer_vote WHERE answerID = ?";
            jdbcTemplate.update(deleteVotesSql, answerID);

        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }

        final String selectPicturePathSql = "SELECT picturePath FROM answer WHERE id = ?";
        String picturePath = jdbcTemplate.queryForObject(selectPicturePathSql, String.class, answerID);
        Path filePath = Paths.get(picturePath);

        try {
            Files.delete(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String deleteSql = "DELETE FROM answer WHERE id = ?";
        try {
            jdbcTemplate.update(deleteSql, answerID);
            return 1;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int voteAnswer(String username, int answerID, int upvote)
    {
        final String selectSql = "SELECT userID FROM answer WHERE id = ?";
        int authorID = jdbcTemplate.queryForObject(selectSql, Integer.class, answerID);

        final String author = jdbcTemplate.queryForObject("SELECT username FROM user WHERE id = ?", String.class, authorID);


        final String selectUserSql = "SELECT id FROM user WHERE username = ?";
        int userID = jdbcTemplate.queryForObject(selectUserSql, Integer.class, username);

        if(author.equals(username)) {
            return 0;
        }

        final String selectSql2 = "SELECT * FROM user_answer_vote WHERE answerID = ? AND userID = ?";

        List<Integer> upvoteList = jdbcTemplate.query(selectSql2, new Object[]{answerID, userID}, (rs, rowNum) -> {
            return rs.getInt("upvote");
        });

        System.out.println(authorID);
        System.out.println(userID);
        System.out.println(answerID);
        System.out.println(upvoteList.size());
        System.out.println(upvote);


        if(upvoteList.size() > 0) {
            if(upvoteList.get(0) == upvote) {
                return 2;
            }
            else {
                final String updateSql = "UPDATE user_answer_vote SET upvote = ? WHERE answerID = ? AND userID = ?";
                jdbcTemplate.update(updateSql, upvote, answerID, userID);
            }
            final String updateSql2 = "UPDATE answer SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateSql2, 2 * upvote, answerID);

            float scoreToAddToUser = 0;
            float scoreToAddToAuthor = 0;

            if(upvote == 1){
                scoreToAddToAuthor = 7.5f;
                scoreToAddToUser = 1.5f;
            }
            else {
                scoreToAddToAuthor = -7.5f;
                scoreToAddToUser = -1.5f;
            }
            final String updateAuthorScoreSql = "UPDATE user SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateAuthorScoreSql, scoreToAddToAuthor, authorID);

            final String updateUserScoreSql = "UPDATE user SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateUserScoreSql, scoreToAddToUser, userID);
        }
        else {
            final String insertSql = "INSERT INTO user_answer_vote (answerID, userID, upvote) VALUES (?,?,?)";
            jdbcTemplate.update(insertSql, answerID, userID, upvote);

            final String updateSql = "UPDATE answer SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateSql, upvote, answerID);

            float scoreToAddToUser = 0;
            float scoreToAddToAuthor = 0;

            if(upvote == 1){
                scoreToAddToAuthor = 5.0f;
            }
            else {
                scoreToAddToAuthor = -2.5f;
                scoreToAddToUser = -1.5f;


                final String updateUserScoreSql = "UPDATE user SET score = score + ? WHERE id = ?";
                jdbcTemplate.update(updateUserScoreSql, scoreToAddToUser, userID);
            }
            final String updateAuthorScoreSql = "UPDATE user SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateAuthorScoreSql, scoreToAddToAuthor, authorID);


        }
        return 1;

    }

    public Answer getAnswerById(Long answerID) {
        try {

            final String selectSql = "SELECT * FROM answer WHERE id = ?";
            Answer answer = jdbcTemplate.queryForObject(selectSql, new Object[]{answerID}, (rs, rowNum) -> {
                Answer a = new Answer();
                a.setId((long) rs.getInt("id"));
                a.setUserID(rs.getInt("userID"));
                a.setText(rs.getString("text"));
                a.setScore(rs.getInt("score"));
                a.setCreationTime(rs.getTimestamp("creationTime").toLocalDateTime());
                a.setPicturePath(rs.getString("picturePath"));
                a.setQuestionID(rs.getInt("questionID"));
                return a;
            });
            answer.setAuthor(jdbcTemplate.queryForObject("SELECT username FROM user WHERE id = ?", String.class, answer.getUserID()));
            return answer;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public int updateAnswer(String username, Long answerID, String text, MultipartFile image) {
        String sql = "SELECT * FROM answer WHERE id = ?";
        Answer answer = jdbcTemplate.queryForObject(sql, new Object[]{answerID}, (rs, rowNum) -> {
            Answer a = new Answer();
            a.setId((long) rs.getInt("id"));
            a.setUserID(rs.getInt("userID"));
            return a;
        });

        if(answer == null) {
            return 0;
        }

        sql = "SELECT id FROM user WHERE username = ?";
        int usernameID = jdbcTemplate.queryForObject(sql, new Object[]{username}, Integer.class);

        if(usernameID != answer.getUserID() && userRepository.verifyIfUserHasARole(username, "ROLE_MODERATOR") == 0){
            return 0;
        }

        if(text == null)
        {
            text = answer.getText();
        }

        final String updateTextSql = "UPDATE answer SET text = ? WHERE id = ?";
        jdbcTemplate.update(updateTextSql, text, answerID);

        if(image != null) {
            String uploadDir = "./images";

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                try {
                    Files.createDirectories(dirPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Path filePath = Paths.get(uploadDir, "A" + answerID + "U" + usernameID + image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4));
            try {
                Files.write(filePath, image.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }

            final String updatePicturePathSql = "UPDATE answer SET picturePath = ? WHERE id = ?";
            jdbcTemplate.update(updatePicturePathSql, filePath.toString(), answerID);
        }

        return 1;
    }

    public List<Answer> getAnswersOfUser(String username) {
        //sort them desc by creation time
        final String selectSql = "SELECT * FROM answer WHERE userID = (SELECT id FROM user WHERE username = ?) ORDER BY creationTime DESC";
        List<Answer> answers = jdbcTemplate.query(selectSql, new Object[]{username}, (rs, rowNum) -> {
            Answer a = new Answer();
            a.setId((long) rs.getInt("id"));
            a.setUserID(rs.getInt("userID"));
            a.setText(rs.getString("text"));
            a.setScore(rs.getInt("score"));
            a.setCreationTime(rs.getTimestamp("creationTime").toLocalDateTime());
            a.setPicturePath(rs.getString("picturePath"));
            a.setQuestionID(rs.getInt("questionID"));
            return a;
        });

        for(Answer a : answers) {
            a.setAuthor(username);
        }
        return answers;
    }
}
























