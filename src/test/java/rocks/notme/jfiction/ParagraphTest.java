package rocks.notme.jfiction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ParagraphTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/paragraph-tests.csv", numLinesToSkip = 1)
    void paragraphTest(String element, String textAlignment, String paramsString) {
        String[] params = paramsString.split(",");
        Paragraph paragraph = new Paragraph(element);
        assumeTrue(params.length / 3 == paragraph.chunks.size());
        assertEquals(TextAlignment.valueOf(textAlignment), paragraph.alignment);
        for (int i = 0; i < paragraph.chunks.size(); i++) {
            assertEquals(params[i * 3], paragraph.chunks.get(i).text);
            assertEquals(Boolean.valueOf(params[1 + (i * 3)]), paragraph.chunks.get(i).em);
            assertEquals(Boolean.valueOf(params[2 + (i * 3)]), paragraph.chunks.get(i).strong);
        }
    }
}
