package com.quiz.Backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User player;

    private int playerScore;

    private LocalDateTime completedDate;

    public Score() {}


    public Score(Tournament tournament, User player, int playerScore, LocalDateTime completedDate) {
        this.tournament = tournament;
        this.player = player;
        this.playerScore = playerScore;
        this.completedDate = completedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }
}
