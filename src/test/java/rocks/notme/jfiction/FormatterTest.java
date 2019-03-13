package rocks.notme.jfiction;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import rocks.notme.jfiction.formatting.StorytextHtmlStringFormatter;
import rocks.notme.jfiction.formatting.UrlToStringFormatter;

class FormatterTest {

    @Test
    void storytextHtmlStringTest() {
        try {
            UrlToStringFormatter testFormatter = new UrlToStringFormatter(this.getClass().getResource("/storytext_test.html"));
            UrlToStringFormatter resultFormatter = new UrlToStringFormatter(this.getClass().getResource("/storytext_result.html"));
            String testHtml = testFormatter.call();
            String resultHtml = resultFormatter.call();
            StorytextHtmlStringFormatter storyFormatter = new StorytextHtmlStringFormatter(testHtml);
            String actualHtml = storyFormatter.call();
            assertEquals(resultHtml, actualHtml);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
