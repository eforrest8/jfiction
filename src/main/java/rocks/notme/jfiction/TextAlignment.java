package rocks.notme.jfiction;

public enum TextAlignment {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    private final String text;
    TextAlignment(String textInterpretation) {
        text = textInterpretation;
    }

    public String getText() {return text;}
}
