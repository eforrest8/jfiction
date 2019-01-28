package rocks.notme.jfiction;

import com.lowagie.text.*;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chapter;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import rocks.notme.jfiction.jfx.FictionUI;

import java.io.FileOutputStream;
import java.time.Instant;
import java.util.Date;

public class Jfiction {
    public static void main(String[] args) {
        //TODO: document everything with javadocs
        //launch the UI
        FictionUI.launch(FictionUI.class);
        //for testing, run the pdf creation method directly. swap the below and above lines out before packaging
        //createPDF("https://www.fanfiction.net/s/2001232/3/", "test");
        //https://www.fanfiction.net/s/1491375/1/ url left here for convenience
    }

    /**
     * This method creates the PDF.
     */
    public static void createPDF(Story story) {
        // step 1: creation of a document-object
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try {
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(sanitizeTitle(story.title) + ".pdf"));
            // step 3: we open the document
            writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
            document.open();

            //FONTS
            FontFactory.defaultEmbedding = true;
            Font normalFont     = FontFactory.getFont(FontFactory.TIMES, 12);
            Font boldFont       = FontFactory.getFont(FontFactory.TIMES_BOLD,12);
            Font italicFont     = FontFactory.getFont(FontFactory.TIMES_ITALIC,12);
            Font boldItalicFont = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC,12);

            //first page stuff
            document.addAuthor(story.author);
            document.addTitle(story.title);
            document.addCreationDate();
            document.add(story.coverArt);
            ColumnText ct = new ColumnText(writer.getDirectContent());
            ct.setSimpleColumn(50, 300, 300, document.top(), 15, Element.ALIGN_LEFT);
            ct.addText(new Chunk(story.title + "\n", FontFactory.getFont(FontFactory.TIMES_BOLD, 16)));
            ct.addText(new Chunk("By " + story.author +"\n", boldFont));
            ct.addText(new Chunk(new LineSeparator()));
            ct.addText(Chunk.NEWLINE);
            ct.addText(new Chunk(story.description, normalFont));
            ct.go();
            //document.add(new Chunk(story.title, FontFactory.getFont(FontFactory.TIMES_BOLD, 16)));
            //document.add(new Chunk(story.author, boldFont));
            //document.add(new Chunk(story.description, normalFont));

            document.resetPageCount();
            document.setMargins(100, 100, 50, 50);
            HeaderFooter header = new HeaderFooter(new Phrase(""), true);
            header.setAlignment(HeaderFooter.ALIGN_CENTER);
            document.setHeader(header);

            int chapternum = 1;
            for (rocks.notme.jfiction.Chapter chapter: story.chapters) {//every chapter
                Paragraph chapTitle = new Paragraph(chapter.name, FontFactory.getFont(FontFactory.TIMES_BOLD, 16));
                chapTitle.setAlignment(Paragraph.ALIGN_CENTER);
                chapTitle.setSpacingAfter(16);
                document.add(new Chapter(chapTitle, chapternum));
                chapternum++;
                for (rocks.notme.jfiction.Paragraph paragraph: chapter) {//every paragraph
                    Paragraph p = new Paragraph();
                    for (rocks.notme.jfiction.Paragraph.StoryChunk chunk: paragraph) {//every chunk
                        if (chunk.text.equals("<hr>"))
                            p.add(new Chunk(new LineSeparator()));
                        else if (chunk.em && chunk.strong)
                            p.add(new Chunk(chunk.text, boldItalicFont));
                        else if (chunk.em)
                            p.add(new Chunk(chunk.text, italicFont));
                        else if (chunk.strong)
                            p.add(new Chunk(chunk.text, boldFont));
                        else
                            p.add(new Chunk(chunk.text, normalFont));
                    }
                    p.setAlignment(paragraph.alignment.getText());
                    p.setSpacingAfter(12);
                    p.setKeepTogether(true);
                    document.add(p);
                }
                document.add(Chunk.NEXTPAGE);
            }
            document.resetHeader();
            //last page; include various metadata here
            document.add(new Paragraph("This document was generated on " +
                    Date.from(Instant.now()).toString() +
                    " by jfiction, and the text and metadata was retrieved based on the following URL.\n" +
                    story.location));
        }
        catch(Exception de) {
            de.printStackTrace();
        }
        // step 5: we close the document
        document.close();
    }

    private static String sanitizeTitle(String input) {
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
