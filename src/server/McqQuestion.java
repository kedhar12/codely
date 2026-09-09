package server;

import java.util.List;

public class McqQuestion {
    public final String id;
    public final String language;
    public final String question;
    public final String codeSnippet;
    public final List<String> options;
    public final int correctIndex;
    public final String explanation;
    public final String difficulty;

    public McqQuestion(
        String id,
        String language,
        String question,
        String codeSnippet,
        List<String> options,
        int correctIndex,
        String explanation,
        String difficulty
    ) {
        this.id = id;
        this.language = language;
        this.question = question;
        this.codeSnippet = codeSnippet != null ? codeSnippet : "";
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation != null ? explanation : "";
        this.difficulty = difficulty != null ? difficulty : "Medium";
    }
}
