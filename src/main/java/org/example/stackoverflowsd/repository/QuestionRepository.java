package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.Answer;
import org.example.stackoverflowsd.model.Question;
import org.example.stackoverflowsd.model.QuestionAnswers;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository implements QuestionInterface {
    public void saveQuestion(QuestionInterface questionInterface) {
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int postQuestion(Question question, MultipartFile image) {
        String sqlQuery = "SELECT id FROM user WHERE username = ?";
        int authorID = jdbcTemplate.queryForObject(sqlQuery, Integer.class, question.getAuthor());


        final String insertSql = "INSERT INTO question (userID, title, text, creationTime, picturePath) VALUES (?,?,?,NOW(),?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
                ps.setInt(1, authorID);
                ps.setString(2, question.getTitle());
                ps.setString(3, question.getText());
                ps.setString(4, question.getPicturePath());
                return ps;
            }, keyHolder);

            int newQuestionID = keyHolder.getKey().intValue();

            String[] tags = question.getTags().split(",");


            for (String tag : tags) {
                tag = tag.toLowerCase();
                tag = tag.replaceAll("^\\s+", "");
                final String insertTagSql = "INSERT INTO tag (name) VALUES (?) ON DUPLICATE KEY UPDATE name=name";
                jdbcTemplate.update(insertTagSql, tag);
            }

            for (String tag : tags) {
                tag = tag.toLowerCase();
                tag = tag.replaceAll("^\\s+", "");
                final String selectTagSql = "SELECT id FROM tag WHERE name = ?";
                int tagID = jdbcTemplate.queryForObject(selectTagSql, Integer.class, tag);
                final String insertQuestionTagSql = "INSERT INTO question_tag_join (question_id, tag_id) VALUES (?,?)";
                jdbcTemplate.update(insertQuestionTagSql, newQuestionID, tagID);
            }


            String uploadDir = "./images";

            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = Paths.get(uploadDir, "Q" + newQuestionID + "U" + authorID + image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4));
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

        int userID = jdbcTemplate.queryForObject("SELECT id FROM user WHERE username = ?", Integer.class, username);

        final String selectSql = "SELECT q.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tagNames " +
                "FROM question q " +
                "LEFT JOIN question_tag_join qt ON q.id = qt.question_id " +
                "LEFT JOIN tag t ON qt.tag_id = t.id " +
                "WHERE q.userID = ? " +
                "GROUP BY q.id " +
                "ORDER BY q.creationTime DESC";

        List<Question> questions = jdbcTemplate.query(selectSql, new Object[]{userID}, (rs, rowNum) -> {
            return new Question(
                    rs.getLong("id"),
                    rs.getInt("userID"),
                    rs.getString("title"),
                    rs.getString("text"),
                    rs.getTimestamp("creationTime").toLocalDateTime(),
                    rs.getString("picturePath"),
                    rs.getString("tagNames"),
                    rs.getInt("score"));
        });

        for(Question question : questions) {
            final String selectSql2 = "SELECT username FROM user WHERE id = ?";
            String author = jdbcTemplate.queryForObject(selectSql2, String.class, question.getUserID());
            question.setAuthor(author);
        }
        return questions;
    }

    public int deleteQuestion(String username, Long questionID) {
        int userID;
        final String selectSql = "SELECT userID FROM question WHERE id = ?";
        try {
            userID = jdbcTemplate.queryForObject(selectSql, Integer.class, questionID);
        }
        catch (Exception e) {
            return 2;
        }

        final String selectUserSql = "SELECT username FROM user WHERE id = ?";
        String author = jdbcTemplate.queryForObject(selectUserSql, String.class, userID);


        if (author.equals(username) || userRepository.verifyIfUserHasARole(username, "ROLE_MODERATOR") == 1) {
            String deleteQuestionTagJoinSql = "DELETE FROM question_tag_join WHERE question_id = ?";
            jdbcTemplate.update(deleteQuestionTagJoinSql, questionID);

            String deleteQuestionVoteSql = "DELETE FROM user_question_vote WHERE questionID = ?";
            jdbcTemplate.update(deleteQuestionVoteSql, questionID);

            final String selectPicturePathSql = "SELECT picturePath FROM question WHERE id = ?";
            String picturePath = jdbcTemplate.queryForObject(selectPicturePathSql, String.class, questionID);
            Path filePath = Paths.get(picturePath);

            try {
                Files.delete(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String selectAnswerIDsSql = "SELECT id FROM answer WHERE questionID = ?";
            List<Integer> answerIDs = jdbcTemplate.queryForList(selectAnswerIDsSql, Integer.class, questionID);

            for (int answerID : answerIDs) {
                String deleteAnswerVoteSql = "DELETE FROM user_answer_vote WHERE answerID = ?";
                jdbcTemplate.update(deleteAnswerVoteSql, answerID);
            }

            String deleteAnswersSql = "DELETE FROM answer WHERE questionID = ?";
            jdbcTemplate.update(deleteAnswersSql, questionID);



            String deleteSql = "DELETE FROM question WHERE id = ?";
            if(jdbcTemplate.update(deleteSql, questionID) == 0) {
                return 2;
            }
            return 1;
        } else {
            return 0;
        }
    }

    public int updateQuestion(String author, Long id, String title, String text, String tags, MultipartFile image) throws IOException {

        try {;

            final String selectSql = "SELECT q.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tagNames " +
                    "FROM question q " +
                    "LEFT JOIN question_tag_join qt ON q.id = qt.question_id " +
                    "LEFT JOIN tag t ON qt.tag_id = t.id " +
                    "WHERE q.id = ? ";

            Question question = jdbcTemplate.queryForObject(selectSql, new Object[]{id}, (rs, rowNum) -> {
                return new Question(
                        rs.getLong("id"),
                        rs.getInt("userID"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getTimestamp("creationTime").toLocalDateTime(),
                        rs.getString("picturePath"),
                        rs.getString("tagNames"),
                        rs.getInt("score"));
            });

            final String selectUserSql = "SELECT username FROM user WHERE id = ?";
            String authorFromID = jdbcTemplate.queryForObject(selectUserSql, String.class, question.getUserID());

            if (!authorFromID.equals(author) && userRepository.verifyIfUserHasARole(author, "ROLE_MODERATOR") == 0) {
                return 0;
            }

            String uploadDir = "./images";

            Path filePath = Paths.get(uploadDir, "Q" + question.getId() + "U" + authorFromID + image.getOriginalFilename().substring(image.getOriginalFilename().length() - 4));
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

            final String deleteQuestionTagJoinSql = "DELETE FROM question_tag_join WHERE question_id = ?";
            jdbcTemplate.update(deleteQuestionTagJoinSql, id);



            String[] tagsArray = tags.split(",");

            for (String tag : tagsArray) {
                tag = tag.toLowerCase();
                final String insertTagSql = "INSERT INTO tag (name) VALUES (?) ON DUPLICATE KEY UPDATE name=name";
                jdbcTemplate.update(insertTagSql, tag);
            }

            for (String tag : tagsArray) {
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

    public List<Question> searchQuestions(String title, String text, String author, String tags) {
        final String selectSql = "SELECT q.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tagNames " +
                "FROM question q " +
                "LEFT JOIN question_tag_join qt ON q.id = qt.question_id " +
                "LEFT JOIN tag t ON qt.tag_id = t.id ";

        StringBuilder whereClause = null;

        int first = 0;

        if (title != null) {
            whereClause = new StringBuilder("WHERE ");
            whereClause.append("q.title LIKE '%").append(title).append("%' ");
            first = 1;
        }
        if (text != null) {
            if (first == 1) {
                whereClause.append("AND ");
            }
            else {
                whereClause = new StringBuilder("WHERE ");
            }
            whereClause.append("q.text LIKE '%").append(text).append("%' ");
            first = 1;
        }
        if (author != null) {
            final String selectUserSql = "SELECT id FROM user WHERE username = ?";
            int authorID = jdbcTemplate.queryForObject(selectUserSql, Integer.class, author);

            if (first == 1) {
                whereClause.append("AND ");
            }
            else {
                whereClause = new StringBuilder("WHERE ");
            }
            whereClause.append("q.userID = ").append(authorID).append(" ");
            first = 1;

        }

        if(first == 0) {
            whereClause = new StringBuilder("");
        }

        List<Question> questions = jdbcTemplate.query(selectSql + whereClause + "GROUP BY q.id ORDER BY q.creationTime DESC", (rs, rowNum) -> {
            return new Question(
                    rs.getLong("id"),
                    rs.getInt("userID"),
                    rs.getString("title"),
                    rs.getString("text"),
                    rs.getTimestamp("creationTime").toLocalDateTime(),
                    rs.getString("picturePath"),
                    rs.getString("tagNames"),
                    rs.getInt("score"));
        });


        if (tags != null) {
            String[] tagsArray = tags.split(",");
            questions.removeIf(question -> {
                for (String tag : tagsArray) {
                    if (!question.getTags().contains(tag)) {
                        return true;
                    }
                }
                return false;
            });
        }

        return questions;
    }

    public int voteQuestion(String username, int questionID, int upvote) {
        final String selectSql = "SELECT userID FROM question WHERE id = ?";
        int userID = jdbcTemplate.queryForObject(selectSql, Integer.class, questionID);

        final String selectUserSql = "SELECT username FROM user WHERE id = ?";
        String author = jdbcTemplate.queryForObject(selectUserSql, String.class, userID);


        if (author.equals(username)) {
            return 0;
        }

        final String selectSql2 = "SELECT * FROM user_question_vote WHERE questionID = ? AND userID = ?";

        List<Integer> upvoteList = jdbcTemplate.query(selectSql2, new Object[]{questionID, userID}, (rs, rowNum) -> {
            return rs.getInt("upvote");
        });

        if(upvoteList.size() > 0) {
            if(upvoteList.get(0) == upvote) {
                return 2;
            }
            else {
                final String updateSql = "UPDATE user_question_vote SET upvote = ? WHERE questionID = ? AND userID = ?";
                jdbcTemplate.update(updateSql, upvote, questionID, userID);
            }
            final String updateSql2 = "UPDATE question SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateSql2, 2 * upvote, questionID);

            float scoreToAdd = 0;
            if(upvote == 1){
                scoreToAdd = 4.0f;
            }
            else {
                scoreToAdd = -4.0f;
            }
            final String updateAuthorScoreSql = "UPDATE user SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateAuthorScoreSql, scoreToAdd, userID);
        }
        else {
            final String insertSql = "INSERT INTO user_question_vote (questionID, userID, upvote) VALUES (?,?,?)";
            jdbcTemplate.update(insertSql, questionID, userID, upvote);

            final String updateSql = "UPDATE question SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateSql, upvote, questionID);

            float scoreToAdd = 0;
            if(upvote == 1){
                scoreToAdd = 2.5f;
            }
            else {
                scoreToAdd = -1.5f;
            }
            final String updateAuthorScoreSql = "UPDATE user SET score = score + ? WHERE id = ?";
            jdbcTemplate.update(updateAuthorScoreSql, scoreToAdd, userID);
        }
        return 1;
    }

    public QuestionAnswers getQuestionDetails(Long questionID) {
        final String selectSql = "SELECT q.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tagNames, u.username " +
                "FROM question q " +
                "LEFT JOIN question_tag_join qt ON q.id = qt.question_id " +
                "LEFT JOIN tag t ON qt.tag_id = t.id " +
                "LEFT JOIN user u ON q.userID = u.id " +
                "WHERE q.id = ? " +
                "GROUP BY q.id";

        final String selectSql2 = "SELECT a.*, u.username " +
                "FROM answer a " +
                "LEFT JOIN user u ON a.userID = u.id " +
                "WHERE a.questionID = ? " +
                "ORDER BY a.score DESC, a.creationTime DESC";

        Question question = jdbcTemplate.queryForObject(selectSql, new Object[]{questionID}, (rs, rowNum) -> {
            return new Question(
                    rs.getLong("id"),
                    rs.getInt("userID"),
                    rs.getString("title"),
                    rs.getString("text"),
                    rs.getTimestamp("creationTime").toLocalDateTime(),
                    rs.getString("picturePath"),
                    rs.getString("tagNames"),
                    rs.getInt("score"));
        });

        final String selectSql3 = "SELECT username FROM user WHERE id = ?";
        String author = jdbcTemplate.queryForObject(selectSql3, String.class, question.getUserID());
        question.setAuthor(author);

        List<Answer> answers = jdbcTemplate.query(selectSql2, new Object[]{questionID}, (rs, rowNum) -> {
            return new Answer(
                    rs.getLong("id"),
                    rs.getInt("userID"),
                    rs.getString("text"),
                    rs.getTimestamp("creationTime").toLocalDateTime(),
                    rs.getString("picturePath"),
                    rs.getInt("score"));
        });

        for(Answer answer : answers) {
            final String selectSql4 = "SELECT username FROM user WHERE id = ?";
            author = jdbcTemplate.queryForObject(selectSql4, String.class, answer.getUserID());
            answer.setAuthor(author);
        }
        return new QuestionAnswers(question, answers);
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


    public Question getQuestionById(Long questionID) {
        try {


            final String selectSql = "SELECT q.*, GROUP_CONCAT(t.name SEPARATOR ', ') AS tagNames " +
                    "FROM question q " +
                    "LEFT JOIN question_tag_join qt ON q.id = qt.question_id " +
                    "LEFT JOIN tag t ON qt.tag_id = t.id " +
                    "WHERE q.id = ? " +
                    "GROUP BY q.id";

            Question question = jdbcTemplate.queryForObject(selectSql, new Object[]{questionID}, (rs, rowNum) -> {
                return new Question(
                        rs.getLong("id"),
                        rs.getInt("userID"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getTimestamp("creationTime").toLocalDateTime(),
                        rs.getString("picturePath"),
                        rs.getString("tagNames"),
                        rs.getInt("score"));
            });

            final String selectSql2 = "SELECT username FROM user WHERE id = ?";
            String author = jdbcTemplate.queryForObject(selectSql2, String.class, question.getUserID());
            question.setAuthor(author);


            return question;
        } catch (Exception e) {
            return null;
        }
    }
}
