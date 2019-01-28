package rocks.notme.jfiction;

import com.lowagie.text.Image;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Story {
    public ArrayList<Chapter> chapters = new ArrayList<>();
    public ArrayList<String> chapterTitles = new ArrayList<>();
    public final String title;
    public final String description;
    public final String author;
    public final String location;
    public final int numChapters;
    public Image coverArt; //TODO: this can probably be final as well
    private final String getChapterFromURL = "/[0-9]{1,2}/";

    public int progress = 0;
    private final ReadOnlyDoubleWrapper storyProgress = new ReadOnlyDoubleWrapper(this, "progress");

    public Story(String id, String newAuthor) throws IOException {
        location = "https://www.fanfiction.net/s/" + id + "/1/";
        author = newAuthor; //TODO: find author automatically
        Document doc = Jsoup.connect(location.replaceFirst(getChapterFromURL, "/1/")).get();
        String generalData = doc.selectFirst(".xgray.xcontrast_txt").text();
        numChapters = Integer.parseInt(generalData.substring(generalData.indexOf("Chapters: ") + 10, generalData.indexOf("Chapters: ") + 12).trim());
        title = doc.selectFirst("b.xcontrast_txt").text();
        description = doc.getElementsByAttributeValue("style", "margin-top:2px").text();
        try { //using a try here to ensure we get an image for the pdf builder
            coverArt = Image.getInstance(new URL("https:" + doc.selectFirst("img.lazy.cimage[width=180]").attributes().getIgnoreCase("data-original")));
            coverArt.setAlignment(Image.RIGHT);
        } catch (Exception e) {
            coverArt = Image.getInstance(new byte[] {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte)0x80, 0x0, 0x0, (byte)0xff, (byte)0xff, (byte)0xff, 0x0, 0x0, 0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b});
            System.err.println("Failed to acquire cover image, using dummy image instead...");
        }
        Elements chapterElements = doc.getElementsByAttributeValue("style", "float:right; ").tagName("option");

        //populate chapterTitles arraylist
        for (int i = 1; i <= numChapters; i++) {
            chapterTitles.add(chapterElements.select("option[value=" + i + "]").text().substring(3));
        }
    }

    public void doLongProcessing() throws IOException {
        for (int i = 1; i <= numChapters; i++) { // swap numChapters to 1 here when testing to not download whole stories
            try {
                TimeUnit.SECONDS.sleep(1); // wait some time before getting each chapter to prevent bandwidth hogging
            }
            catch (InterruptedException e) {
                System.err.println("interrupted, I guess? dunno why this would happen\nException: " + e.getMessage());
            }
            chapters.add(new Chapter(location.replaceFirst(getChapterFromURL, "/" + i + "/"), chapterTitles.get(i - 1)));

            System.out.println("Chapter " + i + " downloaded.");
            storyProgress.setValue(++progress / (numChapters * 2.0));
        }

        ArrayList<Thread> threads = new ArrayList<>();
        for (Chapter c: chapters) {
            threads.add(new Thread(c));
        }
        for (Thread t: threads) {
            t.start();
        }
        try {
            for (Thread t : threads) {
                t.join();
                storyProgress.setValue(++progress / (numChapters * 2.0));
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        /*ExecutorService executor = Executors.newFixedThreadPool(10);
        for (Chapter c: chapters) {
            executor.execute(c);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(numChapters, TimeUnit.MINUTES); //this shouldn't ever take this long, we just want to stop the thread until the executor shuts down
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }*/
    }

    public double getProgress() {
        return storyProgress.doubleValue();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return storyProgress.getReadOnlyProperty();
    }
}
