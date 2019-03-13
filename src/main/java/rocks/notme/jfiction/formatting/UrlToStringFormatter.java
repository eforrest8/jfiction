package rocks.notme.jfiction.formatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Retrieves data from URL and returns it as a String.
 */
public class UrlToStringFormatter implements Formatter<String> {

    private URL url;

    public UrlToStringFormatter(URL url) {
        this.url = url;
    }

    @Override
    public String call() throws IOException {
        InputStream inputStream = url.openConnection().getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
