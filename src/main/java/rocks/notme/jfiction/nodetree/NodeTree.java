package rocks.notme.jfiction.nodetree;

import java.util.ArrayList;

public class NodeTree {
    private GenericNode rootNode = new GenericNode();

    public String getString() {
        return rootNode.getString();
    }

    public void buildTree(ArrayList<String> paragraphs) {
        for (String paragraph: paragraphs) {
            //identify start tag or lack thereof
            String tag;
            String content;
            if (paragraph.startsWith("<p") || paragraph.startsWith("<hr")) {
                tag = paragraph.substring(0, paragraph.indexOf('>') + 1);
                content = paragraph.substring(paragraph.indexOf('>') + 1);
            } else {
                tag = "<p>";
                content = paragraph;
            }
            HTMLNode parNode = new HTMLNode(tag);
            rootNode.addChild(parNode);
            parNode.buildChildren(content);
        }
    }
}
