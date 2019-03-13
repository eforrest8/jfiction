package rocks.notme.jfiction;

public interface Writer {
    abstract public void write(Story story);

    static String sanitizeTitle(String input) {
        input = input.replace('<', '-')
                .replace('>', '-')
                .replace(':', '-')
                .replace('"', '-')
                .replace('/', '-')
                .replace('\\', '-')
                .replace('|', '-')
                .replace('?', '-')
                .replace('*', '-');
        return input;
    }
}
