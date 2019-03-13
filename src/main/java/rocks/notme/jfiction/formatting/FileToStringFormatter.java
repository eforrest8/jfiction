package rocks.notme.jfiction.formatting;

import java.io.*;
import java.util.concurrent.Callable;

public class FileToStringFormatter implements Formatter<String> {

    private File file;

    public FileToStringFormatter(File file) {
        this.file = file;
    }

    @Override
    public String call() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
