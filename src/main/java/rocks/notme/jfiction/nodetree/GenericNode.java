package rocks.notme.jfiction.nodetree;

import java.util.ArrayList;

public class GenericNode implements Node {
    private final ArrayList<Node> children = new ArrayList<>();

    public void addChild(Node node) {
        this.children.add(node);
    }

    @Override
    public String getString() {
        StringBuilder result = new StringBuilder();
        for (Node n : this.children) {
            result.append(n.getString());
        }
        return result.toString();
    }
}
