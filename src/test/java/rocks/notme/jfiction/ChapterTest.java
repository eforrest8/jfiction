package rocks.notme.jfiction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ChapterTest {
    static Document typicalCaseDoc;

    @BeforeAll
    static void initAll() {
        try {
            typicalCaseDoc = Jsoup.parse(new File("typical.html"), "UTF-8");
        } catch (Exception e) {

        }
    }

    @Test
    void chapterTest() {
        // do test
    }
}
