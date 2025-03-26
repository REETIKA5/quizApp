package com.quiz.Backend.repositories;

import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByCategory(String category);
    List<Tournament> findByDifficulty(String difficulty);
    List<Tournament> findByStartDateAfter(LocalDate date);
    List<Tournament> findByEndDateBefore(LocalDate date);
    List<Tournament> findByStatus(TournamentStatus status);  // Filter by status
    List<Tournament> findByStatusAndStartDateBeforeAndEndDateAfter(TournamentStatus status, LocalDateTime now, LocalDateTime now2); // For ongoing tournaments


}