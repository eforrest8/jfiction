package rocks.notme.jfiction;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Chapter implements Iterable<Paragraph>, Runnable {
    private ArrayList<Paragraph> paragraphs = new ArrayList<>();
    private Element storyNode;
    String name;

    public Chapter(String url, String newName) throws IOException {
        name = newName;
        Document doc = Jsoup.connect(url).get();
        storyNode = doc.getElementById("storytext");
    }

    /**
     * Constructor to be used with existing document (mostly for unit testing).
     * @param doc Jsoup Document object containing story text
     * @param newName Chapter title
     */
    public Chapter(Document doc, String newName) {
        name = newName;
        storyNode = doc.body(); //not sure this is right, needs to be tested
    }

    public Iterator iterator() {
        return paragraphs.iterator();
    }

    /**
     * Parses story text and populates paragraph arraylist with resulting objects.
     */
    public void run() {
        Elements storytext = storyNode.children(); // this is all of the <p> elements (or maybe not???)
        //check to see if first element in storytext is a <p>, if not use different parse method
        //if (storyNode.html().substring(0, 2).equals("<p")) {
        if (storyNode.childNodeSize() > 0 && (storyNode.child(0).tagName().equals("p") || storyNode.child(1).tagName().equals("p"))) {
            for (Element e : storytext) {
                paragraphs.add(new Paragraph(e));
            }
        } else { //TODO: rewrite slow parser to handle all busted pages, make it an option in UI
            int startIndex = 0;
            for (int i = 0; i < storyNode.html().length(); i++) {
                if (storyNode.html().charAt(i) == '<' && storyNode.html().charAt(i + 1) == 'b') {
                    paragraphs.add(new Paragraph(storyNode.html().substring(startIndex, i)));
                    try {
                        if (storyNode.html().charAt(i + 5) == '<' && storyNode.html().charAt(i + 6) == 'b') {
                            i += 8; //move index past two <br>s

                        } else {
                            i += 4; //move index past one <br>
                        }
                        startIndex = i; //set startIndex for next paragraph
                    } catch (StringIndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        }
    }
}
