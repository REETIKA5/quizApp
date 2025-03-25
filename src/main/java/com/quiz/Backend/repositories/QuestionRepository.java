package com.quiz.Backend.repositories;

import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTournament(Tournament tournament);
}
