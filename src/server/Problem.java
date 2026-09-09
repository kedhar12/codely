package server;

import java.util.List;
import java.util.Map;

public class Problem {
    public final String id;
    public final String title;
    public final String difficulty;
    public final String category;
    public final String description;
    public final String inputFormat;
    public final String outputFormat;
    public final String constraints;
    public final String sampleInput;
    public final String sampleOutput;
    public final Map<String, String> starterTemplates;
    public final List<TestCase> testCases;

    public Problem(
        String id,
        String title,
        String difficulty,
        String category,
        String description,
        String inputFormat,
        String outputFormat,
        String constraints,
        String sampleInput,
        String sampleOutput,
        Map<String, String> starterTemplates,
        List<TestCase> testCases
    ) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.category = category;
        this.description = description;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.constraints = constraints;
        this.sampleInput = sampleInput;
        this.sampleOutput = sampleOutput;
        this.starterTemplates = starterTemplates;
        this.testCases = testCases;
    }
}
