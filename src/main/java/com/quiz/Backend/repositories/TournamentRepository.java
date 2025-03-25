package com.quiz.Backend.repositories;

import com.quiz.Backend.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByCategory(String category);
    List<Tournament> findByDifficulty(String difficulty);
    List<Tournament> findByStartDateAfter(LocalDate date);
    List<Tournament> findByEndDateBefore(LocalDate date);


}