package rocks.notme.jfiction;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EpubWriter implements Writer {

    @Override
    public void write(Story story) {
        try (
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(Writer.sanitizeTitle(story.title) + ".epub"), StandardCharsets.UTF_8);
                InputStream cssIn = Writer.class.getResourceAsStream("/stylesheet.css");
                InputStream containerXML = Writer.class.getResourceAsStream("/container.xml");
                InputStream coverXML = Writer.class.getResourceAsStream("/cover.xhtml");
                InputStream titlepageXML = Writer.class.getResourceAsStream("/titlepage.xhtml");
                InputStream tocXML = Writer.class.getResourceAsStream("/toc.xhtml");
                InputStream chapterXML = Writer.class.getResourceAsStream("/chapter.xhtml");
                InputStream packageOPF = Writer.class.getResourceAsStream("/package.opf")
        ) {
            byte[] mimetypeBytes = "application/epub+zip".getBytes(StandardCharsets.US_ASCII);
            CRC32 mimeCRC = new CRC32();
            mimeCRC.update(mimetypeBytes);
            ZipEntry mimetypeZipEntry = new ZipEntry("mimetype");
            mimetypeZipEntry.setMethod(ZipEntry.STORED);
            mimetypeZipEntry.setSize(mimetypeBytes.length);
            mimetypeZipEntry.setCrc(mimeCRC.getValue());
            writeZipEntry(
                    mimetypeBytes,
                    mimetypeZipEntry,
                    out
            );

            StringBuilder cover = new StringBuilder(new String(coverXML.readAllBytes()));
            cover.insert(cover.indexOf("<", cover.indexOf("id=\"title\"")), story.title);

            StringBuilder title = new StringBuilder(new String(titlepageXML.readAllBytes()));
            title.insert(title.indexOf("<", title.indexOf("id=\"title\"")), story.title);
            title.insert(title.indexOf("<", title.indexOf("id=\"titleheader\"")), story.title);
            title.insert(title.indexOf("<", title.indexOf("id=\"authorheader\"")), story.author);
            title.insert(title.indexOf("<", title.indexOf("id=\"description\"")), story.description);

            StringBuilder toc = new StringBuilder(new String(tocXML.readAllBytes()));
            toc.insert(toc.indexOf("<", toc.indexOf("id=\"title\"")), story.title);
            toc.insert(toc.indexOf("<", toc.indexOf("id=\"titlelink\"")), story.title);

            StringBuilder packagexml = new StringBuilder(new String(packageOPF.readAllBytes()));
            packagexml.insert(packagexml.indexOf("<", packagexml.indexOf("id=\"title\"")), story.title);
            packagexml.insert(packagexml.indexOf("<", packagexml.indexOf("id=\"creator\"")), story.author);
            packagexml.insert(packagexml.indexOf("<", packagexml.indexOf("id=\"pub-id\"")), story.location);
            packagexml.insert(packagexml.indexOf("<", packagexml.indexOf("property=\"dcterms:modified\"")), "0000-00-00T00:00:00Z");//LocalDateTime.now()); //TODO: figure out how epub spec wants this formatted

            String chapterBase = new String(chapterXML.readAllBytes());

            for (Chapter c: story.chapters) {
                int chapterNumber = story.chapters.indexOf(c) + 1;
                //add lines to table of contents page
                int index = toc.lastIndexOf("</li>") + 5;
                toc.insert(index, "\n" +
                        "<li class=\"toc-Chapter-rw\">\n" +
                        "<a href=\"chapter_" + chapterNumber + ".xhtml\" id=\"toc-chapter_" + chapterNumber + "\">" + c.name +"</a>\n" +
                        "</li>");
                //add lines to package page
                index = packagexml.indexOf("</manifest>");
                packagexml.insert(index, "<item id=\"xchapter_" + chapterNumber + "\" href=\"chapter_" + chapterNumber + ".xhtml\" media-type=\"application/xhtml+xml\"/>\n");
                index = packagexml.indexOf("</spine>");
                packagexml.insert(index, "<itemref idref=\"xchapter_" + chapterNumber + "\" linear=\"yes\"/>");
                //write chapter page
                StringBuilder chapter = new StringBuilder(chapterBase);
                chapter.insert(chapter.indexOf("<", chapter.indexOf("id=\"title\"")), story.title);
                chapter.insert(chapter.indexOf("<", chapter.indexOf("id=\"chaptertitle\"")), c.name);
                chapter.insert(chapter.indexOf("</section>"), c.storyHTML);
                writeZipEntry(
                        chapter.toString().getBytes(),
                        new ZipEntry("OPS/chapter_" + chapterNumber + ".xhtml"),
                        out
                );
            }

            //set up static files and directory structure
            writeZipEntry(
                    containerXML.readAllBytes(),
                    new ZipEntry("META-INF/container.xml"),
                    out
            );
            writeZipEntry(
                    cssIn.readAllBytes(),
                    new ZipEntry("OPS/css/stylesheet.css"),
                    out
            );
            writeZipEntry(
                    story.coverArt.getRawData(),
                    new ZipEntry("OPS/images/cover.jpg"),
                    out
            );
            writeZipEntry(
                    cover.toString().getBytes(),
                    new ZipEntry("OPS/cover.xhtml"),
                    out
            );
            writeZipEntry(
                    title.toString().getBytes(),
                    new ZipEntry("OPS/titlepage.xhtml"),
                    out
            );
            writeZipEntry(
                    toc.toString().getBytes(),
                    new ZipEntry("OPS/toc.xhtml"),
                    out
            );
            //TODO: add a page with various metadata (URL, "generated by a program" text, update date, creation date, etc)
            writeZipEntry(
                    packagexml.toString().getBytes(),
                    new ZipEntry("OPS/package.opf"),
                    out
            );

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeZipEntry(byte[] b, ZipEntry ze, ZipOutputStream out) throws IOException {
        out.putNextEntry(ze);
        out.write(b);
        out.closeEntry();
    }
}
