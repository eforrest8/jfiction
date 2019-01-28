package rocks.notme.jfiction;

import com.lowagie.text.Chunk;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ParagraphTest {

    @ParameterizedTest
    @MethodSource("paragraphProvider")
    void paragraphTest(String element, TextAlignment textAlignment, List<TestParams> params) {
        Paragraph paragraph = new Paragraph(element);
        //assumeTrue(params.size() == paragraph.chunks.size());
        assertEquals(textAlignment, paragraph.alignment);
        for (int i = 0; i < params.size(); i++) {
            assertEquals(params.get(i).text, paragraph.chunks.get(i).text);
            assertEquals(params.get(i).em, paragraph.chunks.get(i).em);
            assertEquals(params.get(i).strong, paragraph.chunks.get(i).strong);
        }
    }

    static Stream<Arguments> paragraphProvider() {
        final String testString = "The quick brown fox jumps over the lazy dog.";
        final Element p = new Element("p").text(testString);
        final Element em = new Element("em").text(testString);
        final Element pem = p.appendChild(em);

        //TODO: create comprehensive data set
        return Stream.of(
                Arguments.of(
                        "<p>" + testString + "</p>",
                        TextAlignment.LEFT,
                        Arrays.asList(new TestParams(testString, false, false))),
                Arguments.of(new Element("p").attr("style", "text-align:center;").text(testString).outerHtml(),
                        TextAlignment.CENTER,
                        Arrays.asList(new TestParams(testString, false, false))),
                Arguments.of(pem.outerHtml(),
                        TextAlignment.LEFT,
                        Arrays.asList(
                                new TestParams(testString, false, false),
                                new TestParams(testString, true, false)
                        ))
        );
    }

    private final static class TestParams {
        final String text;
        final boolean em;
        final boolean strong;
        TestParams(String newText, boolean newEm, boolean newStrong) {
            text = newText;
            em = newEm;
            strong = newStrong;
        }
    }
}
