package com.quiz.Backend.services;

import com.quiz.Backend.dto.OpenTdbResponse;
import com.quiz.Backend.dto.OpenTdbResult;
import com.quiz.Backend.models.Question;
import com.quiz.Backend.models.Tournament;
import com.quiz.Backend.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> fetchQuestionsFromOpenTdb(String category, String difficulty, Tournament tournament) {
        String url = String.format(
                "https://opentdb.com/api.php?amount=10&category=%s&difficulty=%s&type=multiple",
                category, difficulty
        );

        ResponseEntity<OpenTdbResponse> response = restTemplate.getForEntity(url, OpenTdbResponse.class);
        List<Question> questionList = new ArrayList<>();

        if (response.getBody() != null && response.getBody().getResults() != null) {
            for (OpenTdbResult result : response.getBody().getResults()) {
                Question question = new Question(
                        result.getQuestion(),
                        result.getCorrect_answer(),
                        result.getIncorrect_answers(),
                        "multiple-choice",
                        tournament
                );
                questionRepository.save(question);
                questionList.add(question);
            }
        }

        return questionList;
    }

    public List<Question> getQuestionsByTournament(Tournament tournament) {
        return questionRepository.findByTournament(tournament);
    }
}
