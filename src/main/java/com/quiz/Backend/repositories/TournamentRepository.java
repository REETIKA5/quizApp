package com.quiz.Backend.repositories;

import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.models.TournamentStatus;
import com.quiz.Backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;


public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByStartDateAfter(LocalDate date);
    List<Tournament> findByEndDateBefore(LocalDate date);
    List<Tournament> findByStatus(TournamentStatus status);  // Filter by status
    List<Tournament> findByParticipants(User user);
     List<Tournament> findByCategoryAndStatus(String category, TournamentStatus status);

}