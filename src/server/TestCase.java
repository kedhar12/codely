package server;

public class TestCase {
    public final String input;
    public final String expectedOutput;
    public final boolean isHidden;
    public final String explanation;

    public TestCase(String input, String expectedOutput, boolean isHidden, String explanation) {
        this.input = input != null ? input : "";
        this.expectedOutput = expectedOutput != null ? expectedOutput : "";
        this.isHidden = isHidden;
        this.explanation = explanation != null ? explanation : "";
    }
}
