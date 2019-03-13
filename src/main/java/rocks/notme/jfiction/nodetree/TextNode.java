package rocks.notme.jfiction.nodetree;

public class TextNode implements Node {
    private final String text;

    TextNode(String text) {
        this.text = text;
    }

    @Override
    public String getString() {
        return this.text;
    }
}
