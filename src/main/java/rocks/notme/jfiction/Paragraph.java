package rocks.notme.jfiction;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Iterator;

public class Paragraph implements Iterable<Paragraph.StoryChunk> {
    ArrayList<StoryChunk> chunks = new ArrayList<>();
    TextAlignment alignment = TextAlignment.LEFT;

    Paragraph(Element storytext) {
        this(storytext.outerHtml());
    }

    Paragraph(String storytext) {
        //check for text alignment style tags
        if (storytext.contains("text-align:center;")) {
            alignment = TextAlignment.CENTER;
        } else if (storytext.contains("text-align:right;")) {
            alignment = TextAlignment.RIGHT;
        }

        //parse elements to convert text into StoryChunks
        int endNodeIndex = 0;
        int startNodeIndex = 0;
        boolean em = false;
        boolean strong = false;
        for (; endNodeIndex < storytext.length(); endNodeIndex++) {
            if (storytext.charAt(endNodeIndex) == '<' && startNodeIndex == 0) { //first chunk, maybe
                if (storytext.charAt(endNodeIndex + 1) == 'h') {
                    chunks.add(new StoryChunk("<hr>", em, strong));
                } else {
                    chunks.add(new StoryChunk(storytext.substring(startNodeIndex, endNodeIndex)));
                }
                //this block runs if an html tag is being closed at this position
            } else if (storytext.charAt(endNodeIndex) == '<' && storytext.charAt(endNodeIndex + 1) == '/') { //end of node
                chunks.add(new StoryChunk(storytext.substring(startNodeIndex, endNodeIndex), em, strong));
                switch (storytext.charAt(endNodeIndex + 2)) {
                    case 'e':
                        em = false;
                        break;
                    case 's':
                        strong = false;
                        break;
                }
                //this block runs if an html tag is being opened at this position
            } else if (storytext.charAt(endNodeIndex) == '<') {
                chunks.add(new StoryChunk(storytext.substring(startNodeIndex, endNodeIndex), em, strong));
                switch (storytext.charAt(endNodeIndex + 1)) {
                    case 'e':
                        em = true;
                        break;
                    case 's':
                        strong = true;
                        break;
                }
                //this block runs at the end of an html tag
            } else if (storytext.charAt(endNodeIndex) == '>') {
                startNodeIndex = endNodeIndex + 1;
            }
        }
        //add text in case there are not html elements
        if (startNodeIndex < endNodeIndex) {
            chunks.add(new StoryChunk(storytext.substring(startNodeIndex, endNodeIndex), em, strong));
        }
        //remove empty chunks
        ArrayList<StoryChunk> toRemove = new ArrayList<>();
        for (StoryChunk c: chunks) { // ConcurrentModificationException is happening here???
            if (c.text.isEmpty()) {
                toRemove.add(c);
            }
        }
        chunks.removeAll(toRemove);
    }

    public Iterator iterator() {
        return chunks.iterator();
    }

    public class StoryChunk {
        String text = "";
        boolean em; //is italic?
        boolean strong; //is bold?

        StoryChunk(String newText) {
            this(newText, false, false);
        }

        StoryChunk(String newText, boolean newEm, boolean newStrong) {
            text = newText;
            em = newEm;
            strong = newStrong;
        }
    }
}
