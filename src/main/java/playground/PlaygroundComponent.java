package playground;

/** One placed component. Holds type, label, position and (for INPUT) its value. */
public class PlaygroundComponent {

    private final String id;
    private final ComponentType type;
    private String label;
    private Position position;
    private boolean inputValue; // only meaningful for INPUT

    PlaygroundComponent(String id, ComponentType type, String label, Position position) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.position = position;
    }

    public String getId() { return id; }
    public ComponentType getType() { return type; }
    public String getLabel() { return label; }
    public Position getPosition() { return position; }
    public boolean getInputValue() { return inputValue; }

    void setLabel(String label) { this.label = label; }
    void setPosition(Position position) { this.position = position; }
    void setInputValue(boolean value) { this.inputValue = value; }
}
