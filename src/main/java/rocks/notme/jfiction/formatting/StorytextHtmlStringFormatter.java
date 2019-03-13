package rocks.notme.jfiction.formatting;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import rocks.notme.jfiction.nodetree.*;

public class StorytextHtmlStringFormatter implements Formatter<String> {

    private final String html;

    public StorytextHtmlStringFormatter(String html) {
        this.html = html;
    }

    @Override
    public String call() throws Exception { //TODO: rewrite this to use NodeTree
        ArrayList<String> paragraphs = new ArrayList<>();
        NodeTree tree = new NodeTree();

        int currentIndex = 0;
        String validBr = "<br />";
        String[] brVariations = { //hopefully pages only use one of these
                "<br>",
                "<br/>",
                "<br />"
        };
        for (String e: brVariations) {
            if (html.contains(e)) {
                validBr = e;
            }
        }
        do {
            int startIndex = currentIndex;
            int brOffset = 0;
            int brIndex = html.indexOf(validBr + validBr, currentIndex + 1); // this will probably cause problems, but should work for ffn
            int endPIndex = html.indexOf("</p", currentIndex + 1);
            int hrIndex = html.indexOf("<hr", currentIndex + 1);
            Integer[] endIndexes = {
                    html.indexOf("<p", currentIndex + 1), // start of new paragraph
                    hrIndex, // start of <hr> element
                    hrIndex != -1 ? html.indexOf('>', hrIndex) + 1 : -1, // end of <hr> element
                    endPIndex != -1 ? endPIndex + "</p>".length() : -1, // end of previous paragraph
                    brIndex, // start of double <br>
                    html.length() // end of document
            };
            Arrays.sort(endIndexes);
            for (Integer e : endIndexes) {
                if (e != -1 && e > currentIndex) {
                    if (e == brIndex) {
                        brOffset = validBr.length() * 2;
                    }
                    currentIndex = e;
                    break;
                }
            }
            paragraphs.add(html.substring(startIndex, currentIndex));
            currentIndex += brOffset;
        } while (currentIndex != html.length());

        tree.buildTree(paragraphs);
        return tree.getString();
    }
}
