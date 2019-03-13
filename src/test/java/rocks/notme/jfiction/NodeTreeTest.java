package rocks.notme.jfiction;

import org.junit.jupiter.api.Test;
import rocks.notme.jfiction.nodetree.NodeTree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class NodeTreeTest {

    //basic test
    @Test
    void basicTest() {
        ArrayList<String> testInput = new ArrayList<>();
        testInput.add("<p style=\"text-align: center; \">text<em><strong>italic</em><br />more text</p>");
        testInput.add("<p>text2<em>em1</em>middle<em>em2</em>end</p>");
        testInput.add("oops no p tag");
        testInput.add("weir<em>d pa</em>ragraph");
        testInput.add("<hr noshade>");
        NodeTree nodeTree = new NodeTree();
        nodeTree.buildTree(testInput);
        String treeString = nodeTree.getString();
        assertEquals("<p style=\"text-align: center; \">text<em><strong>italic</strong></em><br/>more text</p>" +
                        "<p>text2<em>em1</em>middle<em>em2</em>end</p>" +
                        "<p>oops no p tag</p>" +
                        "<p>weir<em>d pa</em>ragraph</p>" +
                        "<hr noshade=\"noshade\"/>",
                treeString);
    }
}
