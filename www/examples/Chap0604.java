/*
 * $Id$
 * $Name$
 *
 * This code is free software. It may only be copied or modified
 * if you include the following copyright notice:
 *
 * --> Copyright 2001 by Bruno Lowagie <--
 *
 * This code is part of the 'iText Tutorial'.
 * You can find the complete tutorial at the following address:
 * http://www.lowagie.com/iText/tutorial/
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * itext-questions@lists.sourceforge.net
 */

import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

public class Chap0604 {
    public static void main(String[] args) {
        System.out.println("Chapter 6 example 4: Alignment of images");
        // step 1: creation of a document-object
        Document document = new Document();
        try {
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            PdfWriter.getInstance(document, new FileOutputStream("Chap0604.pdf"));
            
            // step 3: we open the document
            document.open();
            
            Image gif = Image.getInstance("vonnegut.gif");
            gif.setAlignment(Image.RIGHT);
            Image jpeg = Image.getInstance("myKids.jpg");
            jpeg.setAlignment(Image.MIDDLE);
            Image png = Image.getInstance("hitchcock.png");
            png.setAlignment(Image.LEFT);
            
            document.add(gif);
            document.add(jpeg);
            document.add(png);
        }
        catch(DocumentException de) {
            System.err.println(de.getMessage());
        }
        catch(IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        // step 5: we close the document
        document.close();
    }
}