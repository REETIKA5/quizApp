package com.quiz.Backend.dto;

import java.util.List;


public class OpenTdbResponse {
    private int response_code;
    private List<OpenTdbResult> results;

    public OpenTdbResponse() {}

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public List<OpenTdbResult> getResults() {
        return results;
    }

    public void setResults(List<OpenTdbResult> results) {
        this.results = results;
    }
}
