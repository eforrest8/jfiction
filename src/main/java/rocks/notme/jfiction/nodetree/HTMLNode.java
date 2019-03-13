package rocks.notme.jfiction.nodetree;

import java.util.ArrayList;
import java.util.HashMap;

public class HTMLNode implements Node {
    private final String name;
    private final boolean isVoid;
    private final HashMap<String, String> attributes;
    private final ArrayList<Node> children = new ArrayList<>();

    public HTMLNode(String tag) throws RuntimeException{
        if (tag.charAt(1) == '/') {
            throw new RuntimeException("Can't construct HTMLNode with closing tag");
        } else {
            this.name = getTagName(tag);
            this.isVoid = this.name.equals("br") || this.name.equals("hr");
            this.attributes = new HashMap<>();

            for (int i = this.name.length() + 1; i < tag.length(); i++) {
                if (tag.charAt(i) == '/' || tag.charAt(i) == '>') { //end of tag
                    break;
                } else {
                    if (tag.charAt(i) != ' ' && tag.charAt(i - 1) == ' ') { //start of attribute key
                        int equalsIndex = tag.indexOf('=', i);
                        if ((equalsIndex < tag.indexOf(' ', i)) &&
                                (equalsIndex != -1)) { //this key has a value
                            String key = tag.substring(i, equalsIndex);
                            String value = tag.substring(equalsIndex + 2, tag.indexOf('"', equalsIndex + 2));
                            this.attributes.put(key, value);
                            i = tag.indexOf('"', equalsIndex + 2);
                        } else { //this key does not have a value eg. <hr noshade/>
                            int[] indexArray = {
                                    tag.indexOf(' ', i),
                                    tag.indexOf('/', i),
                                    tag.indexOf('>', i)};
                            int keyEndIndex = tag.length() - 1;
                            for (int e: indexArray) {
                                if (e != -1) {
                                    keyEndIndex = e;
                                    break;
                                }
                            }
                            String key = tag.substring(i, keyEndIndex);
                            this.attributes.put(key, key);
                            i = keyEndIndex;
                        }
                    }
                }
            }
        }
    }

    public HTMLNode(String name, boolean isVoid, HashMap<String, String> attributes) {
        this.name = name;
        this.isVoid = isVoid;
        this.attributes = attributes;
    }

    public void addChild(Node node) throws RuntimeException {
        if (this.isVoid) {
            throw new RuntimeException("Cannot add child to void node");
        } else {
            this.children.add(node);
        }
    }

    @Override
    public String getString() {
        StringBuilder result = new StringBuilder();
        result.append("<").append(this.name);
        this.attributes.forEach((String key, String value) -> {
            result.append(" ").append(key).append("=\"").append(value).append("\"");
        });
        if (this.isVoid) {
            result.append("/>");
        } else {
            result.append(">");
            for (Node n : this.children) {
                result.append(n.getString());
            }
            result.append("</").append(this.name).append(">");
        }
        return result.toString();
    }

    private String getTagName(String tag) {
        int startIndex = 1;
        int endIndex;
        Character[] charArray = {' ', '/', '>'};
        for (Character c: charArray) {
            endIndex = tag.indexOf(c);
            if (endIndex != -1)
                return tag.substring(startIndex, endIndex);
        }
        endIndex = tag.length() - 1;
        return tag.substring(startIndex, endIndex);
    }

    public void buildChildren(String content) {
        int startIndex = 0;
        int i = 0;
        for (; i < content.length(); i++) {
            if (content.charAt(i) == '>') {
                startIndex = i + 1;
            }
            if (content.charAt(i) == '<') { //start of tag
                if (startIndex != i) { // not a blank string
                    this.addChild(new TextNode(content.substring(startIndex, i)));
                }

                startIndex = i;
                i = content.indexOf('>', startIndex) + 1;
                if (content.charAt(startIndex + 1) != '/') { // we are not looking at an end tag
                    HTMLNode newNode = new HTMLNode(content.substring(startIndex, i));
                    this.addChild(newNode);

                    startIndex = i;
                    if (!newNode.isVoid) { // newNode can contain children
                        int closingTagIndex = content.indexOf("</" + newNode.name, startIndex);
                        i = closingTagIndex == -1 ?
                                content.length() :
                                closingTagIndex;
                        newNode.buildChildren(content.substring(startIndex, i));
                    }
                }
                startIndex = i;
            }
        }
        if (!this.isVoid && startIndex != i - 1) {
            this.addChild(new TextNode(content.substring(startIndex)));
        }
    }
}
