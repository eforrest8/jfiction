package rocks.notme.jfiction;

public enum WriterType {
    PDF("PDF"),
    EPUB("EPUB");

    private final String text;
    WriterType(String textInterpretation) {
        text = textInterpretation;
    }

    public String toString() {return text;}
}
