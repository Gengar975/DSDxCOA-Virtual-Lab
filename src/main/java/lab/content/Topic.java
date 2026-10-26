package lab.content;

import java.util.ArrayList;
import java.util.List;

/**
 * One learnable topic: the written lesson (theory, key points, uses, worked example).
 * Pure data - no JavaFX - so the content can be tested and reused by any UI.
 */
public final class Topic {

    /** The sections shown in the sidebar. */
    public enum Group {
        GATES("Logic Gates", "DSD"),
        BOOLEAN("Boolean Algebra", "DSD"),
        NUMBERS("Number Systems & Codes", "DSD"),
        COMBINATIONAL("Combinational Circuits", "DSD"),
        SEQUENTIAL("Sequential Circuits", "DSD"),
        COA("Computer Organization", "COA"),
        PLAYGROUND("Circuit Playground", "Playground");

        private final String title;
        private final String lab;

        Group(String title, String lab) {
            this.title = title;
            this.lab = lab;
        }

        public String getTitle() { return title; }
        /** "DSD", "COA" or "Playground". */
        public String getLab() { return lab; }
    }

    private final String id;
    private final Group group;
    private final String title;
    private final String tagline;
    private final List<String> theory = new ArrayList<>();
    private final List<String> keyPoints = new ArrayList<>();
    private final List<String> usedIn = new ArrayList<>();
    private String exampleTitle = "";
    private final List<String> exampleLines = new ArrayList<>();

    private Topic(String id, Group group, String title, String tagline) {
        this.id = id;
        this.group = group;
        this.title = title;
        this.tagline = tagline;
    }

    public static Topic of(String id, Group group, String title, String tagline) {
        return new Topic(id, group, title, tagline);
    }

    public Topic theory(String... paragraphs) { theory.addAll(List.of(paragraphs)); return this; }
    public Topic points(String... p) { keyPoints.addAll(List.of(p)); return this; }
    public Topic uses(String... u) { usedIn.addAll(List.of(u)); return this; }

    public Topic example(String title, String... lines) {
        this.exampleTitle = title;
        exampleLines.addAll(List.of(lines));
        return this;
    }

    public String getId() { return id; }
    public Group getGroup() { return group; }
    public String getTitle() { return title; }
    public String getTagline() { return tagline; }
    public List<String> getTheory() { return List.copyOf(theory); }
    public List<String> getKeyPoints() { return List.copyOf(keyPoints); }
    public List<String> getUsedIn() { return List.copyOf(usedIn); }
    public String getExampleTitle() { return exampleTitle; }
    public List<String> getExampleLines() { return List.copyOf(exampleLines); }
}
