package org.example.stackoverflowsd.repository;

import org.example.stackoverflowsd.model.Question;
import org.springframework.data.repository.CrudRepository;

public interface AnswerInterface extends CrudRepository<Question, Integer> {
}
