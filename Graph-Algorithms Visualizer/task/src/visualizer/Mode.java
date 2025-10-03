package visualizer;

public enum Mode {
    ADD_VERTEX("Add a Vertex"),
    ADD_EDGE("Add an Edge"),
    REMOVE_A_VERTEX("Remove a Vertex"),
    REMOVE_AN_EDGE("Remove an Edge"),
    NONE("None");

    private final String displayName;

    Mode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

